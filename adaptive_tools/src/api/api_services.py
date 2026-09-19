import os
import uuid
import base64
import requests
import urllib3
import json
from dotenv import load_dotenv
from typing import Union, List, Dict
from concurrent.futures import ThreadPoolExecutor

CACHE_FILE = "synonyms_cache.json"

def load_synonyms_cache() -> dict:
    if os.path.exists(CACHE_FILE):
        with open(CACHE_FILE, "r", encoding="utf-8") as f:
            return json.load(f)
    return {}

def save_synonyms_cache(cache: dict):
    with open(CACHE_FILE, "w", encoding="utf-8") as f:
        json.dump(cache, f, ensure_ascii=False, indent=4)

# Глушим ругань на отсутствие SSL сертификатов
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

load_dotenv()

def get_fresh_gigachat_token(auth_key: str | None = None) -> str:
    """
    Добывает свежий access_token. 
    Он превратится в тыкву ровно через 30 минут.
    """
    auth_key = auth_key or os.getenv("GIGACHAT_AUTH_TOKEN")
    if not auth_key:
        raise ValueError("Нет GIGACHAT_AUTH_TOKEN. Без Base64-ключа авторизация не пройдет.")

    url = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth"
    
    headers = {
        "Content-Type": "application/x-www-form-urlencoded",
        "Accept": "application/json",
        "RqUID": str(uuid.uuid4()),
        "Authorization": f"Basic {auth_key}"
    }
    
    payload = {"scope": "GIGACHAT_API_PERS"}
    
    try:
        response = requests.post(url, headers=headers, data=payload, verify=False, timeout=10)
        response.raise_for_status()
        
        token = response.json().get("access_token")
        if token:
            os.environ["GIGACHAT_ACCESS_TOKEN"] = token
            return token
            
    except requests.RequestException as e:
        print(f"[!] Сбер отклонил запрос на токен: {e}")
        if e.response is not None:
            print(f"[!] Ответ сервера: {e.response.text}")
            
    return ""

def translate_text(text: str, lang: str = "rus2tat", token: str | None = None) -> str:
    """Выполняет перевод текста с помощью API TatSoft 2.0."""
    url = "https://v2.api.translate.tatar/listening/"
    params = {"lang": lang, "text": text}

    if token:
        params["token"] = token

    response = requests.get(url, params=params, timeout=10)
    response.raise_for_status()

    return response.json()

def synthesize_speech(
    text: str, 
    speaker: str = "alsu", 
    token: str | None = None, 
    decode_bytes: bool = True
) -> Union[bytes, dict]:
    """Синтезирует речь через API TatSoft."""
    url = "https://tat-tts.api.translate.tatar/listening/"
    
    if speaker not in ("alsu", "almaz"):
        raise ValueError("Параметр speaker должен быть 'alsu' или 'almaz'")
        
    params = {"speaker": speaker, "text": text}
    
    if token:
        params["token"] = token
        
    response = requests.get(url, params=params, timeout=15)
    response.raise_for_status()
    
    raw_data = response.json()
    
    if decode_bytes:
        audio_base64 = raw_data.get("wav_base64", "")
        if not audio_base64:
            raise ValueError("API не вернул поле wav_base64")
        return base64.b64decode(audio_base64)
        
    return raw_data

def analyze_morphology(text: str) -> Union[List[Dict], Dict]:
    """Отправляет текст на морфологический анализ."""
    url = "https://tugantel.tatar/new2022/morph?json=data"
    payload = {"text": text}
    
    response = requests.post(url, data=payload, timeout=10)
    response.raise_for_status()
    
    return response.json()

def get_russian_synonyms(word: str, rus_context: str, limit: int = 10) -> list[str]:
    """Вытаскивает контекстные синонимы через GigaChat API с автообновлением токена."""
    url = "https://gigachat.devices.sberbank.ru/api/v1/chat/completions"
    
    prompt = (
        f"Ты лингвист, упрощающий тексты. Подбери {limit} МАКСИМАЛЬНО ПРОСТЫХ "
        f"и базовых синонимов для слова '{word}' в контексте: '{rus_context}'. "
        f"Синонимы должны быть понятны начинающим (уровень А1-А2). "
        f"Обязательно включи само исходное слово '{word}' в итоговый список. "
        "Отсортируй все слова строго по возрастанию сложности: от самых простых к более сложным. "
        "Выдай ТОЛЬКО слова через запятую, в начальной форме. Никаких вступлений, нумерации, точек и лишнего текста."
    )
    
    payload = {
        "model": "GigaChat",
        "messages": [{"role": "user", "content": prompt}],
        "temperature": 0.1, 
        "max_tokens": 50
    }

    auth_token = os.getenv("GIGACHAT_ACCESS_TOKEN")
    if not auth_token:
        print("[*] Нет активного токена, получаю новый...")
        auth_token = get_fresh_gigachat_token()
        if not auth_token:
            return []

    headers = {
        "Content-Type": "application/json",
        "Accept": "application/json",
        "Authorization": f"Bearer {auth_token}"
    }

    try:
        response = requests.post(url, headers=headers, json=payload, verify=False, timeout=10)
        
        if response.status_code == 401:
            print("[*] Токен протух (401). Запрашиваю новый...")
            new_token = get_fresh_gigachat_token()
            if not new_token:
                return []
            headers["Authorization"] = f"Bearer {new_token}"
            response = requests.post(url, headers=headers, json=payload, verify=False, timeout=10)
            
        response.raise_for_status()
        
        content = response.json()["choices"][0]["message"]["content"]
        
        # Парсим ответ, отрезая точки, которые LLM так любит ставить в конце
        synonyms = [s.strip(" .\n").lower() for s in content.split(",") if s.strip()]
        return [s for s in synonyms if s != word.lower()][:limit]
        
    except (requests.RequestException, KeyError) as e:
        print(f"[!] Отвал GigaChat API или парсинга: {e}")
        return []

def synonimize(tatar_word: str, tatar_context: str) -> list[str]:
    """Тот самый франкенштейн: Тат -> Рус -> Синонимы -> Тат (с кэшированием)."""
    tatar_word_lower = tatar_word.lower()
    
    # Сначала проверяем кэш, чтобы не палить лимиты и время
    cache = load_synonyms_cache()
    if tatar_word_lower in cache:
        print(f"[*] Корень '{tatar_word_lower}' взят из локального кэша.")
        return cache[tatar_word_lower]

    print(f"[*] Исходное слово: {tatar_word} в контексте: {tatar_context}")
    
    # 1. Перевод на русский (костыль с 'затычкой' оставляем, если он решает баги API)
    rus_word = translate_text(tatar_word + ". затычка", lang="tat2rus").lower().split('.')[0].strip()
    rus_context = translate_text(tatar_context, lang="tat2rus").lower()
    print(f"[*] Перевод на русский: {rus_word} в контексте: {rus_context}")

    # 2. Поиск русских синонимов
    rus_synonyms = get_russian_synonyms(rus_word, rus_context)
    if not rus_synonyms:
        print("[!] GigaChat не вернул синонимов.")
        return []
        
    if rus_word and rus_word not in rus_synonyms:
        rus_synonyms.append(rus_word)

    print(f"[*] Найдены русские синонимы: {rus_synonyms}")

    # 3. Перевод синонимов обратно на татарский
    tat_synonyms = set()
    with ThreadPoolExecutor(max_workers=10) as executor:
        results = executor.map(lambda w: translate_text(w, lang="rus2tat").lower().strip(" .\n"), rus_synonyms)

    for tat_syn in results:
        if tat_syn and tat_syn != tatar_word_lower:
            tat_synonyms.add(tat_syn)
            
    final_result = list(tat_synonyms)
    
    # Записываем результат в кэш
    cache[tatar_word_lower] = final_result
    save_synonyms_cache(cache)
    
    return final_result


# ФУНКЦИЯ ДЛЯ ОТЛАДКИ
def print_formatted_morph(analyzed_text: str):
    """Парсит сырую строку разбора и выводит в виде таблицы."""
    lines = [line.strip() for line in analyzed_text.strip().split('\n')]
    print(f"{'Токен':<15} | {'Морфология'}")
    print("-" * 60)
    for i in range(0, len(lines), 2):
        word = lines[i]
        tags = lines[i+1] if i + 1 < len(lines) else ""
        print(f"{word:<15} | {tags}")

if __name__ == "__main__":
    word = "ярчаллы"
    text = "Минем әнием бик ярчаллы, аңа нибары 16 яшь."
    
    print("[*] 1. Тестируем морфологический анализатор TuganTel")
    try:
        morph_result = analyze_morphology(text)
        
        if isinstance(morph_result, dict) and "analyzed" in morph_result:
            print_formatted_morph(morph_result["analyzed"])
        else:
            print(f"[!] API вернул неожиданный формат: {morph_result}")
    except Exception as e:
        print(f"[!] Ошибка анализатора: {e}")

    print("\n" + "="*60 + "\n")
    
    print("[*] 2. Тестируем пайплайн синонимизации (первый запуск - API, второй - кэш)")
    result_1 = synonimize(word, text)
    print("\nИтог 1 (API):", result_1)
    
    print("\nПовторный вызов для проверки кэша...")
    result_2 = synonimize(word, text)
    print("\nИтог 2 (Кэш):", result_2)
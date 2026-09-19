import os
import uuid
import base64
import requests
import urllib3
from dotenv import load_dotenv
from typing import Union, List, Dict
from concurrent.futures import ThreadPoolExecutor
import pymupdf
import re
import sys
from collections import Counter
from pathlib import Path
from pypdf import PdfReader

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
load_dotenv()

env_root = Path(__file__).resolve().parents[3]
if str(env_root) not in sys.path:
    sys.path.insert(0, str(env_root))

from tatlib.adaptive_tools.src.api.api_services import synonimize
from tatlib.adaptive_tools.src.metrics.word_complexity import parse_pdf_freq_dict, get_word_complexity
from tatlib.adaptive_tools.src.metrics.text_complexity import analyze_text_complexity, fetch_and_score_word

import re
import concurrent.futures

# Предполагается, что fetch_and_score_word, synonimize и get_word_complexity уже импортированы.
# Если fetch_and_score_word утеряна, она должна возвращать dict: {"word": str, "score": float, "morphemes": int}

def is_soft_word(word: str) -> bool:
    """Определяет мягкость слова по последней гласной."""
    vowels = "аоуыәөүеияюэ"
    soft_vowels = "әөүеи" # 'и' часто выступает как нечкә
    for char in reversed(word.lower()):
        if char in vowels:
            return char in soft_vowels
    return False # По умолчанию калын (твердое)

def analyze_morphology(text: str) -> Union[List[Dict], Dict]:
    """Отправляет текст на морфологический анализ."""
    url = "https://tugantel.tatar/new2022/morph?json=data"
    payload = {"text": text}
    
    response = requests.post(url, data=payload, timeout=10)
    response.raise_for_status()
    
    return response.json()

def extract_morphemes_from_word(word: str) -> List[str]:
    """Получает морфемы слова через API TuganTel."""
    try:
        # 1. Запрашиваем данные
        morph_data = analyze_morphology(word)
        
        # 2. Парсим JSON (обычно TuganTel отдает список словарей)
        morph_string = ""
        if isinstance(morph_data, list) and len(morph_data) > 0:
            # Ищем ключ 'morph', 'analysis' или 'lemmas' (зависит от точной схемы их JSON)
            # В TuganTel чаще всего это ключ 'morph'
            morph_string = morph_data[0].get("morph", "")
        elif isinstance(morph_data, dict):
            morph_string = morph_data.get("morph", "")
            
        # 3. Чистим и бьем по плюсам твоей функцией
        return get_morpheme_list_from_tugantel(morph_string)
        
    except Exception as e:
        print(f"[!] Ошибка морфоанализа для '{word}': {e}")
        return []

def get_morpheme_list_from_tugantel(morph_string: str) -> List[str]:
    if not morph_string or morph_string.strip() in ["Type1", "Type2", "Num", ""]:
        return []
    first_variant = morph_string.split(';')[0].strip()
    return first_variant.split('+')

def adapt_tatar_suffix(root: str, original_suffix: str) -> str:
    """Адаптирует суффикс под новый корень согласно правилам татарского языка."""
    if not original_suffix:
        return ""

    # 1. Сингармонизм (Гармония гласных)
    vowel_map_to_soft = {'а': 'ә', 'ы': 'е', 'у': 'ү', 'о': 'ө'}
    vowel_map_to_hard = {'ә': 'а', 'е': 'ы', 'ү': 'у', 'ө': 'о'}
    
    soft_root = is_soft_word(root)
    adapted_suf = list(original_suffix.lower())
    
    for i, char in enumerate(adapted_suf):
        if soft_root and char in vowel_map_to_soft:
            adapted_suf[i] = vowel_map_to_soft[char]
        elif not soft_root and char in vowel_map_to_hard:
            adapted_suf[i] = vowel_map_to_hard[char]
            
    adapted_suf = "".join(adapted_suf)
    
    # 2. Ассимиляция согласных (Стык морфем)
    voiceless = "пфктшсхчцщһ"
    nasals = "мнң"
    
    last_char = root[-1].lower() if root else ""
    first_char = adapted_suf[0]
    new_first_char = first_char

    # Күплек сан (множественное число): -лар/-ләр, -нар/-нәр
    if first_char in "лн":
        new_first_char = 'н' if last_char in nasals else 'л'
        
    # Урын-вакыт, чыгыш килешләре: -да/-тә, -дан/-тән, үткән заман: -ды/-те
    elif first_char in "дт":
        new_first_char = 'т' if last_char in voiceless else 'д'
        
    # Барыш килеше: -га/-кә
    elif first_char in "гк":
        new_first_char = 'к' if last_char in voiceless else 'г'
        
    # Затланыш (например, -быз/-пыз)
    elif first_char in "бп":
        new_first_char = 'п' if last_char in voiceless else 'б'

    return new_first_char + adapted_suf[1:]

def attach_suffixes(root: str, suffixes: list) -> str:
    """Последовательно приклеивает массив суффиксов к корню с адаптацией."""
    current_word = root
    for suf in suffixes:
        adapted_suf = adapt_tatar_suffix(current_word, suf)
        current_word += adapted_suf
    return current_word

def simplify_text(text: str, target_complexity: float, freq_dict: dict, max_workers: int = 10) -> str:
    print(f"\n{'='*50}\n[ПАЙПЛАЙН] Старт упрощения. Целевая сложность: {target_complexity}\n{'='*50}")
    
    # 1. Токенизация с сохранением разделителей
    tokens = re.split(r'([а-яёәөүҗңһА-ЯЁӘӨҮҖҢҺ\-]+)', text)
    
    # Исключаем все слова, начинающиеся с заглавной буквы
    unique_words = {
        t for t in tokens 
        if re.fullmatch(r'[а-яёәөүҗңһА-ЯЁӘӨҮҖҢҺ\-]+', t, re.IGNORECASE) and not t[0].isupper()
    }
    
    if not unique_words:
        print("[*] Нет слов для обработки (все с большой буквы или текст пуст).")
        return text

    # 2. Многопоточный скоринг оригинальных слов (TuganTel)
    word_scores = {}
    print(f"[ЭТАП 1] Скоринг {len(unique_words)} уникальных слов (потоки: {max_workers})...")
    with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = {executor.submit(fetch_and_score_word, w, freq_dict): w for w in unique_words}
        for future in concurrent.futures.as_completed(futures):
            res = future.result()
            word_scores[res["word"].lower()] = res["score"]

    # 3. Отбор кандидатов на замену
    complex_words = [w for w in unique_words if word_scores.get(w.lower(), 0.0) > target_complexity]
    print(f"[*] Найдено сложных слов для замены: {len(complex_words)}")
    
    if not complex_words:
        print("[*] Текст уже достаточно простой. Замены не требуются.")
        return text

    # 4. ПОСЛЕДОВАТЕЛЬНЫЙ поиск синонимов (чтобы не дудосить GigaChat)
    synonyms_map = {}
    print(f"[ЭТАП 2] Поиск синонимов (строго последовательно)...")
    for i, w in enumerate(complex_words, 1):
        print(f"  [{i}/{len(complex_words)}] Запрос синонимов для '{w}'...")
        try:
            synonyms_map[w] = synonimize(w, text)
        except Exception as e:
            print(f"[!] Ошибка синонимизации '{w}': {e}")
            synonyms_map[w] = []

    # 5. Сбор новых уникальных синонимов
    all_new_synonyms = set()
    for syns in synonyms_map.values():
        all_new_synonyms.update([s.lower() for s in syns])
    
    unscored_synonyms = {s for s in all_new_synonyms if s not in word_scores}

    # 6. Многопоточный скоринг синонимов (TuganTel)
    if unscored_synonyms:
        print(f"[ЭТАП 3] Скоринг {len(unscored_synonyms)} новых синонимов (потоки: {max_workers})...")
        with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as executor:
            futures = {executor.submit(fetch_and_score_word, s, freq_dict): s for s in unscored_synonyms}
            for future in concurrent.futures.as_completed(futures):
                res = future.result()
                word_scores[res["word"].lower()] = res["score"]

    # 7. Фильтрация и выбор лучшего синонима
    words_to_replace = {}
    for w in complex_words:
        syns = synonyms_map.get(w, [])
        if not syns:
            continue
            
        orig_score = word_scores[w.lower()]
        valid_syns = []
        
        for s in syns:
            s_score = word_scores.get(s.lower(), 1.0)
            if s_score < orig_score:
                distance = abs(s_score - target_complexity)
                valid_syns.append((s, s_score, distance))
                
        if valid_syns:
            valid_syns.sort(key=lambda x: x[2])
            best_synonym_root = valid_syns[0][0] 
            
            # --- ВРЕЗКА МОРФОЛОГИИ ---
            morphemes = extract_morphemes_from_word(w.lower())
            original_suffixes = morphemes[1:] if len(morphemes) > 1 else []
            adapted_synonym = attach_suffixes(best_synonym_root, original_suffixes)
            # -------------------------
            
            words_to_replace[w.lower()] = adapted_synonym
            print(f"[*] Упрощение: '{w}' -> '{best_synonym_root}' -> Итог: '{adapted_synonym}'")

    # 8. Сборка итогового текста с восстановлением регистра
    result_tokens = []
    for token in tokens:
        if re.fullmatch(r'[а-яёәөүҗңһА-ЯЁӘӨҮҖҢҺ\-]+', token, re.IGNORECASE):
            t_lower = token.lower()
            # Поскольку слова с большой буквы мы вообще не брали в candidates, 
            # они сюда попадут только через ветку else и останутся нетронутыми.
            if t_lower in words_to_replace and not token[0].isupper():
                new_word = words_to_replace[t_lower]
                result_tokens.append(new_word)
            else:
                result_tokens.append(token)
        else:
            result_tokens.append(token)

    simplified_text = "".join(result_tokens)
    print(f"\n{'='*50}\n[ФИНАЛ] Текст упрощен.\n{'='*50}")
    return simplified_text

# Пример вызова:
FREQ_DICT = parse_pdf_freq_dict('chastota.pdf')
text = "Мин татар телен өйрәнү өчен ун перспектив юнәлеш сайладым. Әлеге юнәлешләр Минем гамәли тәҗрибәмә бик яхшы төшә."
new_text = simplify_text(text, target_complexity=0.6, freq_dict=FREQ_DICT, max_workers=10)
print(new_text)
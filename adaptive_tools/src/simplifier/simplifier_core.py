import json
import concurrent.futures

import sys
from pathlib import Path

env_root = Path(__file__).resolve().parents[3]
if str(env_root) not in sys.path:
    sys.path.insert(0, str(env_root))

from tatlib.adaptive_tools.src.api.api_services import synonimize
from tatlib.adaptive_tools.src.metrics.word_complexity import parse_pdf_freq_dict

# Предполагается, что extract_morphemes_from_word уже есть в твоем коде
def build_and_save_root_dict(original_freq_dict: dict, output_file="root_freq_dict.json", max_workers=10):
    print(f"[*] Старт конвертации словаря. Слов: {len(original_freq_dict)}")
    root_dict = {}
    
    def process_word(word, rank):
        morphemes = extract_morphemes_from_word(word)
        root = morphemes[0].lower() if morphemes else word.lower()
        return root, rank

    with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = {executor.submit(process_word, w, rank): w for w, rank in original_freq_dict.items()}
        for i, future in enumerate(concurrent.futures.as_completed(futures), 1):
            root, rank = future.result()
            # Оставляем минимальную позицию (чем меньше цифра, тем чаще встречается)
            if root not in root_dict or rank < root_dict[root]:
                root_dict[root] = rank
            
            if i % 100 == 0:
                print(f"  [~] Обработано {i} слов...")

    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(root_dict, f, ensure_ascii=False, indent=2)
    print(f"[+] Готово! Уникальных корней: {len(root_dict)}. Сохранено в {output_file}")
    return root_dict

import os
import re
import json
import requests
import urllib3
import concurrent.futures
from typing import Union, List, Dict, Tuple
from collections import Counter

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

# --- Блок морфологии и грамматики (твои функции, слегка причесанные) ---

def is_soft_word(word: str) -> bool:
    vowels = "аоуыәөүеияюэ"
    soft_vowels = "әөүеи"
    for char in reversed(word.lower()):
        if char in vowels:
            return char in soft_vowels
    return False 

def analyze_morphology(text: str) -> Union[List[Dict], Dict]:
    url = "https://tugantel.tatar/new2022/morph?json=data"
    response = requests.post(url, data={"text": text}, timeout=10)
    response.raise_for_status()
    return response.json()

def get_morpheme_list_from_tugantel(morph_string: str) -> List[str]:
    if not morph_string or morph_string.strip() in ["Type1", "Type2", "Num", ""]:
        return []
    return morph_string.split(';')[0].strip().split('+')

def extract_morphemes_from_word(word: str) -> List[str]:
    try:
        morph_data = analyze_morphology(word)
        morph_string = ""
        if isinstance(morph_data, list) and len(morph_data) > 0:
            morph_string = morph_data[0].get("morph", "")
        elif isinstance(morph_data, dict):
            morph_string = morph_data.get("morph", "")
        return get_morpheme_list_from_tugantel(morph_string)
    except Exception:
        return []

def get_root_and_suffixes(word: str) -> Tuple[str, List[str]]:
    """Вытаскивает корень и список суффиксов из слова."""
    morphemes = extract_morphemes_from_word(word.lower())
    if not morphemes:
        return word.lower(), []
    return morphemes[0], morphemes[1:]

def adapt_tatar_suffix(root: str, original_suffix: str) -> str:
    if not original_suffix:
        return ""
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
    voiceless = "пфктшсхчцщһ"
    nasals = "мнң"
    
    last_char = root[-1].lower() if root else ""
    first_char = adapted_suf[0]
    new_first_char = first_char

    if first_char in "лн":
        new_first_char = 'н' if last_char in nasals else 'л'
    elif first_char in "дт":
        new_first_char = 'т' if last_char in voiceless else 'д'
    elif first_char in "гк":
        new_first_char = 'к' if last_char in voiceless else 'г'
    elif first_char in "бп":
        new_first_char = 'п' if last_char in voiceless else 'б'

    return new_first_char + adapted_suf[1:]

def attach_suffixes(root: str, suffixes: list) -> str:
    current_word = root
    for suf in suffixes:
        current_word += adapt_tatar_suffix(current_word, suf)
    return current_word

# --- Новая логика скоринга от корня ---

def get_root_complexity(root: str, root_freq_dict: dict, max_rank: int = 20000) -> float:
    """
    Считает сложность от 0.0 до 1.0 на основе КОРНЯ.
    Учитывает: позицию корня в частотном словаре и его длину.
    """
    rank = root_freq_dict.get(root.lower(), max_rank)
    
    # Нормализация позиции (чем больше rank, тем реже слово -> ближе к 1.0)
    freq_score = min(rank / max_rank, 1.0)
    
    # Нормализация длины корня (корни > 9 символов считаем сложными)
    len_score = min(len(root) / 9.0, 1.0)
    
    # Веса можно крутить. Сейчас частотность важнее длины (70% на 30%)
    return (freq_score * 0.7) + (len_score * 0.3)


# --- Пайплайн ---

def simplify_text_by_roots(text: str, target_complexity: float, root_freq_dict: dict, max_workers: int = 10, min_len: int = 6) -> str:
    print(f"\n{'='*50}\n[ПАЙПЛАЙН] Старт. Цель: {target_complexity}, Мин. длина: {min_len}\n{'='*50}")
    
    tokens = re.split(r'([а-яёәөүҗңһА-ЯЁӘӨҮҖҢҺ\-]+)', text)
    unique_words = {
        t for t in tokens 
        if re.fullmatch(r'[а-яёәөүҗңһА-ЯЁӘӨҮҖҢҺ\-]+', t, re.IGNORECASE) and len(t) >= min_len
    }
    
    if not unique_words:
        print("[*] Нет слов, подходящих под критерии длины. Выход.")
        return text

    # 1. Морфоанализ (оставляем потоки, это не GigaChat)
    word_data = {}
    print(f"[ЭТАП 1] Морфоанализ {len(unique_words)} слов...")
    with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = {executor.submit(get_root_and_suffixes, w): w for w in unique_words}
        for future in concurrent.futures.as_completed(futures):
            orig_word = futures[future]
            try:
                root, suffixes = future.result()
                score = get_root_complexity(root, root_freq_dict)
                word_data[orig_word.lower()] = {
                    "root": root,
                    "suffixes": suffixes,
                    "score": score
                }
            except Exception as e:
                print(f"[!] Ошибка морфологии для '{orig_word}': {e}")

    complex_words = [w for w, data in word_data.items() if data["score"] > target_complexity]
    print(f"[*] Найдено слов со сложными корнями: {len(complex_words)}")
    if not complex_words:
        return text

    # 3. Синонимизация корней (СТРОГО ПОСЛЕДОВАТЕЛЬНО)
    synonyms_map = {}
    print(f"[ЭТАП 2] Запрос синонимов для {len(complex_words)} корней (синхронно)...")
    for w in complex_words:
        root = word_data[w]["root"]
        try:
            synonyms_map[w] = synonimize(root, text)
        except Exception as e:
            print(f"[!] Ошибка API синонимов для '{root}': {e}")
            synonyms_map[w] = []

    # 4. Скоринг синонимов (снова можно в потоки для морфологии)
    all_synonyms = set(s for syns in synonyms_map.values() for s in syns)
    syn_morph_cache = {}
    
    if all_synonyms:
        print(f"[ЭТАП 3] Морфоанализ {len(all_synonyms)} уникальных синонимов...")
        with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as executor:
            futures = {executor.submit(get_root_and_suffixes, s): s for s in all_synonyms}
            for future in concurrent.futures.as_completed(futures):
                s = futures[future]
                try:
                    syn_morph_cache[s] = future.result()
                except Exception:
                    syn_morph_cache[s] = (s, []) 

    words_to_replace = {}
    print(f"[ЭТАП 4] Выбор лучших корневых синонимов...")
    for w in complex_words:
        syns = synonyms_map.get(w, [])
        if not syns:
            continue
            
        orig_score = word_data[w]["score"]
        valid_syns = []
        
        for s in syns:
            syn_root, _ = syn_morph_cache.get(s, (s, []))
            syn_score = get_root_complexity(syn_root, root_freq_dict)
            
            if syn_score < orig_score:
                distance = abs(syn_score - target_complexity)
                valid_syns.append((syn_root, syn_score, distance))
                
        if valid_syns:
            valid_syns.sort(key=lambda x: x[2])
            best_synonym_root = valid_syns[0][0]
            original_suffixes = word_data[w]["suffixes"]
            
            adapted_synonym = attach_suffixes(best_synonym_root, original_suffixes)
            words_to_replace[w] = adapted_synonym
            print(f"[*] Замена: [{word_data[w]['root']}] -> [{best_synonym_root}] | {w} -> {adapted_synonym}")

    # 5. Сборка текста
    result_tokens = []
    for token in tokens:
        if re.fullmatch(r'[а-яёәөүҗңһА-ЯЁӘӨҮҖҢҺ\-]+', token, re.IGNORECASE):
            t_lower = token.lower()
            if t_lower in words_to_replace:
                new_word = words_to_replace[t_lower]
                if token.istitle():
                    new_word = new_word.capitalize()
                elif token.isupper():
                    new_word = new_word.upper()
                result_tokens.append(new_word)
            else:
                result_tokens.append(token)
        else:
            result_tokens.append(token)

    print(f"\n{'='*50}\n[ФИНАЛ] Текст пересобран.\n{'='*50}")
    return "".join(result_tokens)

# old_dict = parse_pdf_freq_dict('chastota.pdf')
# # Отрезаем первые 500 элементов
# build_and_save_root_dict(old_dict, "root_freq_dict.json")

with open("tatlib/data/cache/root_freq_dict.json", "r", encoding="utf-8") as f:
    ROOT_FREQ_DICT = json.load(f)
 
text = "Ярлы, акчаны алып, өенә кайткан. Бераздан аңа бүтән бер дусты килгән. Ул да ярдәм сораган. Балаларны бүген ашатырлык та акчам юк, дип зарланган."
new_text = simplify_text_by_roots(text, target_complexity=0.6, root_freq_dict=ROOT_FREQ_DICT)
print(new_text)
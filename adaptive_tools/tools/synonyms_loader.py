import sqlite3
import re
import pandas as pd
from tqdm import tqdm
import sys
from pathlib import Path
env_root = Path(__file__).resolve().parents[2]
if str(env_root) not in sys.path:
    sys.path.insert(0, str(env_root))
from tatlib.adaptive_tools.src.api.api_services import synonimize 

def populate_cache_from_txt(txt_path: str, min_len: int = 6):
    print(f"[*] Подключение к текстовому файлу: {txt_path}...")
    try:
        with open(txt_path, 'r', encoding='utf-8') as f:
            raw_text = f.read()
    except FileNotFoundError:
        print(f"[!] Файл {txt_path} не найден.")
        return

    # Убиваем поэтическое форматирование, склеиваем в сплошной текст
    flat_text = re.sub(r'\s+', ' ', raw_text).strip()
    
    # Бьем на логические предложения для адекватного контекста LLM
    sentences = [s.strip() for s in re.split(r'(?<=[.!?…])\s+', flat_text) if s.strip()]
    
    unique_words = {}
    
    print("[*] Извлечение слов и контекста...")
    for sentence in sentences:
        # Достаем все слова с татарской кириллицей
        words = re.findall(r'[а-яёәөүҗңһА-ЯЁӘӨҮҖҢҺ\-]+', sentence, re.IGNORECASE)
        for w in words:
            w_lower = w.lower()
            # Запоминаем слово и его первое встретившееся предложение
            if len(w_lower) >= min_len and w_lower not in unique_words:
                unique_words[w_lower] = sentence

    total_words = len(unique_words)
    print(f"[*] Найдено уникальных слов (от {min_len} букв): {total_words}. Старт API...")

    for word, context in tqdm(unique_words.items(), total=total_words, desc="txt -> синонимы"):
        try:
            synonimize(tatar_word=word, tatar_context=context)
        except Exception as e:
            print(f"\n[!] Сбой на слове '{word}': {e}")
            continue

    print(f"\n[+] Парсинг {txt_path} завершен.")

def populate_cache_from_sqlite(db_path: str, min_len: int = 6):
    print(f"[*] Подключение к БД: {db_path}...")
    conn = sqlite3.connect(db_path)
    
    # Читаем таблицу tokens
    df = pd.read_sql_query("SELECT * FROM tokens", conn)
    conn.close()
    
    if df.empty:
        print("[!] Таблица пустая или не найдена.")
        return

    # 1. Склеиваем предложения для контекста
    # Группируем по text_block_id и сортируем по position
    print("[*] Восстановление контекста из токенов...")
    contexts = {}
    for block_id, group in df.groupby('text_block_id'):
        sorted_group = group.sort_values('position')
        # Собираем оригинальные слова обратно в предложение
        contexts[block_id] = " ".join(sorted_group['word'].astype(str).tolist())

    # 2. Вытаскиваем уникальные слова для синонимизации
    # Берем только normalized_word, отсекаем дубликаты
    unique_words_df = df.drop_duplicates(subset=['normalized_word']).copy()
    
    # Отсекаем короткие слова (меньше min_len букв) и пустые строки
    unique_words_df = unique_words_df[
        (unique_words_df['normalized_word'].str.len() >= min_len) & 
        (unique_words_df['normalized_word'].notna())
    ]
    
    # Опционально: можно фильтровать по estimated_level, если хочешь обрабатывать только C1/B2
    # unique_words_df = unique_words_df[unique_words_df['estimated_level'].isin(['C1', 'B2'])]
    
    total_words = len(unique_words_df)
    print(f"[*] Уникальных слов (от {min_len} букв): {total_words}. Старт API...")

    # 3. Прогон через API с записью в json
    for _, row in tqdm(unique_words_df.iterrows(), total=total_words, desc="Генерация синонимов"):
        word = str(row['normalized_word'])
        block_id = row['text_block_id']
        context = contexts.get(block_id, word) 
        
        try:
            # Твоя функция уже содержит проверку кэша и сохранение в synonyms_cache.json
            synonimize(tatar_word=word, tatar_context=context)
        except Exception as e:
            print(f"\n[!] Сбой на слове '{word}': {e}")
            continue

    print("\n[+] Сбор синонимов завершен. Проверь synonyms_cache.json.")

if __name__ == "__main__":
    # Укажи путь к своему файлу базы
    DB_FILE = "tatlib/data/tatar_adaptive.db"
    TXT_FILE = "tatlib/data/example/gabdulla_poem.txt"
    MIN_LENGTH = 6
    # populate_cache_from_sqlite(DB_FILE, min_len=MIN_LENGTH)
    populate_cache_from_txt(TXT_FILE, min_len=MIN_LENGTH)
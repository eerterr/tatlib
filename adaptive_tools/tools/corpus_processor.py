import pandas as pd
import re
import json
from tqdm import tqdm
import sys
from pathlib import Path

env_root = Path(__file__).resolve().parents[2]
if str(env_root) not in sys.path:
    sys.path.insert(0, str(env_root))

from tatlib.adaptive_tools.src.simplifier.simplifier_core import simplify_text_by_roots 

def clean_and_split(raw_text: str) -> list[str]:
    """Убирает маркдаун и бьет текст на адекватные предложения."""
    if not isinstance(raw_text, str):
        return []
        
    # Сносим #, \n, лишние пробелы и знаки копирайта
    text = re.sub(r'[#©]', '', raw_text)
    text = re.sub(r'\n+', ' ', text)
    text = re.sub(r'\s+', ' ', text).strip()
    
    # Бьем по точкам, восклицательным и вопросительным знакам
    sentences = re.split(r'(?<=[.!?…])\s+', text)
    
    # Оставляем только те предложения, где есть хотя бы 3 слова, чтобы не парсить мусор
    return [s.strip() for s in sentences if len(s.split()) >= 3]

def process_parquet_corpus(
    parquet_path: str, 
    dict_path: str, 
    target_complexity: float = 0.6,
    limit_books: int = 0
):
    print(f"[*] Загрузка словаря из {dict_path}...")
    with open(dict_path, "r", encoding="utf-8") as f:
        root_freq_dict = json.load(f)

    print(f"[*] Чтение датасета {parquet_path}...")
    df = pd.read_parquet(parquet_path)
    
    if limit_books > 0:
        df = df.head(limit_books)
        print(f"[*] Ограничение включено: обрабатываем только первые {limit_books} записей.")

    total_sentences = 0
    
    # Итерируемся по каждой книге/тексту в датафрейме
    for idx, row in tqdm(df.iterrows(), total=len(df), desc="Обработка текстов"):
        sentences = clean_and_split(row['text'])
        
        for sentence in sentences:
            total_sentences += 1
            try:
                # Нам не обязательно сохранять результат, 
                # цель — триггернуть кэширование синонимов внутри функции
                simplify_text_by_roots(
                    text=sentence, 
                    target_complexity=target_complexity, 
                    root_freq_dict=root_freq_dict,
                    max_workers=5 # Снизил потоки, чтобы GigaChat не откинул 429 Too Many Requests
                )
            except Exception as e:
                print(f"\n[!] Ошибка на предложении '{sentence[:30]}...': {e}")
                continue
                
    print(f"\n[+] Готово. Пропущено предложений через пайплайн: {total_sentences}")

if __name__ == "__main__":
    # Укажи свои пути
    PARQUET_FILE = "lib-books.parquet"
    DICT_FILE = "sliced_root_freq_dict.json"
    
    # Поставь limit_books=5 для первого теста, иначе будешь ждать до завтра
    process_parquet_corpus(
        parquet_path=PARQUET_FILE,
        dict_path=DICT_FILE,
        target_complexity=0.6,
        limit_books=2 
    )
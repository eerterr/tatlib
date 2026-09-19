import sqlite3
import pandas as pd
import sys
import json
from pathlib import Path
from tqdm import tqdm

env_root = Path(__file__).resolve().parents[2]
if str(env_root) not in sys.path:
    sys.path.insert(0, str(env_root))

from tatlib.adaptive_tools.src.simplifier.simplifier_core import simplify_text_by_roots
from tatlib.adaptive_tools.src.api.api_services import translate_text

DB_PATH = 'tatlib/data/tatar_adaptive.db'
TABLE_NAME = 'text_blocks'

conn = sqlite3.connect(DB_PATH)
df = pd.read_sql_query(f"SELECT * FROM {TABLE_NAME};", conn)

# 1. Грузим словарь один раз, а не для каждой строки
with open("tatlib/data/cache/root_freq_dict.json", "r", encoding="utf-8") as f:
    ROOT_FREQ_DICT = json.load(f)

def adapt_text(text, target_complexity=0.5):
    return simplify_text_by_roots(text, target_complexity, ROOT_FREQ_DICT)

# 2. Обертка для варварского распила
def process_long_text(func, text):
    if not isinstance(text, str) or not text:
        return text
    mid = len(text) // 2
    return func(text[:mid]) + func(text[mid:])

# 3. Безопасное создание столбцов (чтобы не затереть уже обработанное)
if 'adapted_text' not in df.columns:
    df['adapted_text'] = None
if 'russian_text' not in df.columns:
    df['russian_text'] = None

for index, row in tqdm(df.iterrows(), total=len(df), desc="Обработка текста"):
    # Скипаем то, что уже переведено и адаптировано
    # if pd.notna(row.get('adapted_text')) and pd.notna(row.get('russian_text')):
    #     continue

    original = row['text']
    
    try:
        adapted = process_long_text(adapt_text, original)
        # Передаем лямбду, чтобы прокинуть аргумент "tat2rus"
        translated = process_long_text(lambda x: translate_text(x, "tat2rus"), adapted)
        
        df.at[index, 'adapted_text'] = adapted
        df.at[index, 'russian_text'] = translated
        
    except Exception as e:
        print(f"\nОтвал на id {row['id']}: {e}")
        break

df.to_sql(TABLE_NAME, conn, if_exists='replace', index=False)
conn.close()
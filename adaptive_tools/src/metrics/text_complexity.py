import re
import math
import requests
import concurrent.futures
from typing import Dict, Any
import sys
from pathlib import Path
env_root = Path(__file__).resolve().parents[3]
if str(env_root) not in sys.path:
    sys.path.insert(0, str(env_root))

from tatlib.adaptive_tools.src.metrics.word_complexity import parse_pdf_freq_dict

# Заглушка для демонстрации. Используй свой спарсенный словарь.
FREQ_DICT = {"мин": 10, "һәм": 1, "китап": 50, "глобальләштерүчеләрдән": 6000}
MAX_FREQ_RANK = 5000

def get_morpheme_count_from_tugantel(morph_string: str) -> int:
    if not morph_string or morph_string.strip() in ["Type1", "Type2", "Num", ""]:
        return 1
    first_variant = morph_string.split(';')[0].strip()
    return len(first_variant.split('+'))

def get_word_complexity(word: str, morpheme_count: int, freq_dict: dict) -> float:
    word_lower = word.lower()
    length = len(word_lower)
   
    rank = freq_dict.get(word_lower, MAX_FREQ_RANK + 1000)
    freq_penalty = math.log1p(rank) / math.log1p(MAX_FREQ_RANK + 1000)
    len_penalty = min(length / 15.0, 1.0)
    morph_penalty = min(morpheme_count / 5.0, 1.0)
   
    return min((freq_penalty * 0.50) + (len_penalty * 0.15) + (morph_penalty * 0.35), 1.0)

def fetch_and_score_word(word: str, freq_dict: dict) -> dict:
    """Атомарная функция для пула потоков: дергает API и считает скор слова."""
    print(f"[API] ⏳ Отправка запроса для слова: '{word}'...")
    try:
        resp = requests.post(
            "https://tugantel.tatar/new2022/morph?json=data",
            data={"text": word},
            timeout=3
        )
        morph_raw = resp.text
        m_count = get_morpheme_count_from_tugantel(morph_raw)
        print(f"[API] ✅ Успех для '{word}': {m_count} морфем(ы).")
    except Exception as e:
        print(f"[API] ❌ Отвал для '{word}': {e}. Ставим 1 морфему.")
        m_count = 1
       
    score = get_word_complexity(word, m_count, freq_dict)
    print(f"[СКОР] 📊 '{word}' -> сложность: {score:.4f}")
    return {"word": word, "score": score, "morphemes": m_count}

def analyze_text_complexity(text: str, freq_dict: dict, max_workers: int = 10) -> Dict[str, Any]:
    print(f"\n{'='*50}\n[СТАРТ] Анализ текста ({len(text)} символов)\n{'='*50}")
   
    sentences = [s for s in re.split(r'[.!?]+', text) if s.strip()]
    words_raw = re.findall(r'[а-яёәөүҗңһА-ЯЁӘӨҮҖҢҺ\-]+', text.lower())
    words = [w.strip("-") for w in words_raw if w.strip("-")]
   
    print(f"[ПАРСИНГ] Найдено предложений: {len(sentences)}")
    print(f"[ПАРСИНГ] Найдено татарских слов: {len(words)}")
   
    if not words:
        print("[ОШИБКА] Текст пуст или не содержит татарских слов.")
        return {"error": "Текст не содержит татарских слов или пуст."}

    print(f"\n[ПОТОКИ] Запуск пула. max_workers={max_workers}")
    word_metrics = []
   
    with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = {executor.submit(fetch_and_score_word, w, freq_dict): w for w in words}
        for i, future in enumerate(concurrent.futures.as_completed(futures), 1):
            res = future.result()
            word_metrics.append(res)
            print(f"[ПОТОКИ] 🔄 Прогресс: {i}/{len(words)} обработано")

    print("\n[АГРЕГАЦИЯ] Расчет итоговых метрик...")
    avg_word_score = sum(r["score"] for r in word_metrics) / len(word_metrics)
    avg_sentence_len = len(words) / max(len(sentences), 1)
   
    unique_words = len(set(words))
    ttr = unique_words / len(words)
   
    in_dict_percent = sum(1 for w in words if w in freq_dict) / len(words)
   
    print(f"[МЕТРИКА] Средняя сложность слов: {avg_word_score:.4f}")
    print(f"[МЕТРИКА] Средняя длина предложения: {avg_sentence_len:.2f}")
    print(f"[МЕТРИКА] Лексическое разнообразие (TTR): {ttr:.4f}")
    print(f"[МЕТРИКА] Доля слов в словаре: {in_dict_percent:.2%}")
   
    sentence_penalty = min(avg_sentence_len / 20.0, 1.0)
    out_of_dict_penalty = 1.0 - in_dict_percent
   
    text_complexity = (avg_word_score * 0.5) + (sentence_penalty * 0.3) + (out_of_dict_penalty * 0.2)
    print(f"\n[ФИНАЛ] Итоговая сложность текста: {text_complexity:.4f}\n{'='*50}\n")

    return {
        "text_complexity_score": round(text_complexity, 4),
        "avg_word_complexity": round(avg_word_score, 4),
        "avg_sentence_length": round(avg_sentence_len, 2),
        "lexical_diversity_ttr": round(ttr, 4),
        "in_dict_percent": round(in_dict_percent, 4)
    }

if __name__ == "__main__":
    sample_text = "2003 елда физик культура, спорт һәм туризмны үстерү беренче чиратта, җәмгыятьнең сәламәт, физик яктан таза яшь буынга мохтаҗ булуы белән бәйле. Ул сәламәт тормыш белән яшәү, халыкның барлык катламнары өчен файдалы ял оештыру юнәлешендә тиешле шартлар тудыруны күздә тотарга тиеш."
    FREQ_DICT = parse_pdf_freq_dict('tatlib/data/raw/chastota.pdf')
    result = analyze_text_complexity(sample_text, FREQ_DICT, max_workers=5)
   
    for key, value in result.items():
        print(f"{key}: {value}")
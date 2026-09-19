import pymupdf
import re
from collections import Counter
import math
from pypdf import PdfReader

def extract_words_from_pdf(pdf_path: str) -> dict:
    """Вытаскивает слова из PDF с учетом татарского алфавита."""
    try:
        doc = pymupdf.open(pdf_path)
    except Exception as e:
        print(f"[!] Ошибка чтения файла: {e}")
        return {}

    raw_text = ""
    for page in doc:
        raw_text += str(page.get_text())

    # Склеиваем слова, разорванные переносом на новую строку
    clean_text = re.sub(r'-\n\s*', '', raw_text)
    
    # Регулярка под татарскую кириллицу (плюс дефис для составных слов)
    # Игнорирует цифры, латиницу и пунктуацию
    words = re.findall(r'[а-яёәөүҗңһА-ЯЁӘӨҮҖҢҺ\-]+', clean_text.lower())
    
    # Отсекаем тире, прилипшие по краям, и пустые строки
    cleaned_words = [w.strip("-") for w in words if w.strip("-")]

    return dict(Counter(cleaned_words))


def parse_pdf_freq_dict(pdf_path: str) -> dict:
    """
    Парсит PDF со списком слов и возвращает словарь {слово: ранг}.
    Ожидает формат строк типа "1. һәм", "2. белән" и т.д.
    """
    freq_dict = {}
    
    try:
        reader = PdfReader(pdf_path)
        full_text = ""
        
        # Вытаскиваем весь текст со всех страниц
        for page in reader.pages:
            full_text += page.extract_text() + "\n"
            
        # Регулярка ищет паттерн: число, точка, пробел, слово
        # Например: "1. һәм"
        # \d+ - одно или несколько чисел
        # \. - точка
        # \s* - возможные пробелы
        # ([а-яА-ЯёЁәөүҗңһӘӨҮҖҢҺa-zA-Z\-]+) - само слово (татарские буквы включены)
        pattern = re.compile(r'\d+\.\s*([а-яА-ЯёЁәөүҗңһӘӨҮҖҢҺa-zA-Z\-]+)')
        
        matches = pattern.findall(full_text)
        
        for index, word in enumerate(matches, start=1):
            # Переводим в нижний регистр для единообразия
            clean_word = word.lower().strip()
            # Записываем только первое вхождение (если вдруг в PDF дубли)
            if clean_word not in freq_dict:
                freq_dict[clean_word] = index
                
        print(f"Успешно извлечено {len(freq_dict)} уникальных слов.")
        return freq_dict

    except FileNotFoundError:
        print(f"Ошибка: Файл '{pdf_path}' не найден.")
        return {}
    except Exception as e:
        print(f"Ошибка при парсинге PDF: {e}")
        return {}

MAX_FREQ_RANK = 5000

def get_morpheme_count_from_tugantel(morph_string: str) -> int:
    """
    Считает морфологическую сложность по сырому выводу TuganTel.
    """
    # Если это пунктуация или числа (Type1, Type2, Num)
    if not morph_string or morph_string.strip() in ["Type1", "Type2", "Num", ""]:
        return 1
        
    # Разбор может выдавать несколько вариантов через точку с запятой.
    # Пример: 'яшь+Adj;яшь+N+Sg+Nom;'
    # Берем первый (или можно брать самый длинный для пессимистичной оценки)
    first_variant = morph_string.split(';')[0].strip()
    
    # Считаем количество сущностей, разделенных плюсом.
    # Пример: 'әни+N+Sg+POSS_1SG(Ым)+Nom' -> 5 элементов
    blocks = first_variant.split('+')
    
    return len(blocks)

def get_word_complexity(word: str, morpheme_count: int | None = None, freq_dict: dict = {}) -> float:
    """
    Возвращает скор сложности слова от 0.0 до 1.0.
    Чем ближе к 1.0, тем сложнее слово.
    """
    word_lower = word.lower()
    length = len(word_lower)
    if morpheme_count is None:
        # morpheme_count = get_morpheme_count_from_tugantel(word_lower)
        morpheme_count = 0
    # 1. Штраф за редкость
    # Если слова нет в словаре, даем ему ранг за пределами топ-5000
    rank = freq_dict.get(word_lower, MAX_FREQ_RANK + 1000)
    
    # Логарифмическая нормализация от 0 до 1
    # Самое частое слово (ранг 1) получит ~0, самое редкое ~1
    freq_penalty = math.log1p(rank) / math.log1p(MAX_FREQ_RANK + 1000)
    
    # 2. Штраф за длину
    # Урезаем на 15 символах (все, что длиннее 15 букв, получает максимум сложности по этому параметру)
    len_penalty = min(length / 15.0, 1.0)
    
    # 3. Штраф за морфемы (корень + аффиксы)
    # Нормализуем по порогу в 5 морфем (1 корень + 4 суффикса — это уже сложно)
    morph_penalty = min(morpheme_count / 5.0, 1.0)
    
    # Взвешенная сумма. Коэффициенты подбирай руками под метрику качества.
    # Сейчас частотность дает половину веса, так как редкий короткий корень сложнее длинного, но известного слова.
    w_freq = 0.50
    w_len = 0.15
    w_morph = 0.35
    print(f"{word_lower}: частота={freq_penalty}, длина={len_penalty}, морфемы={morph_penalty}")
    complexity = (freq_penalty * w_freq) + (len_penalty * w_len) + (morph_penalty * w_morph)
    
    return min(complexity, 1.0)

# Демонстрация
# Сравниваем частотное короткое слово и редкое слово с кучей аффиксов


if __name__ == "__main__":
    # pdf_file = "grade_1_literature.pdf"
    # word_freq = extract_words_from_pdf(pdf_file)
    
    # # Сортируем по убыванию частоты
    # sorted_words = sorted(word_freq.items(), key=lambda x: x[1], reverse=True)
    # print(sum(word_freq.values()))
    # for word, count in sorted_words[:20]:
    #     print(f"{word}: {count}")

    # Есть датасет книг "lib-books.parquet", также есть датасет предложений-текстов "HF_dataset_500k.parquet"

    # TODO:
    FREQ_DICT = parse_pdf_freq_dict('tatlib/data/raw/chastota.pdf')
    word = "да"
    print(FREQ_DICT[word])
    
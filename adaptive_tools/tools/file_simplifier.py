import os
import sys
from pathlib import Path
env_root = Path(__file__).resolve().parents[2]
if str(env_root) not in sys.path:
    sys.path.insert(0, str(env_root))

from tatlib.adaptive_tools.src.simplifier.simplifier_word_v0 import simplify_text, parse_pdf_freq_dict

def simplify_poem_from_file(input_path: str, output_path: str, target_complexity: float, freq_dict: dict):
    """Считывает txt, скармливает его упрощатору, сохраняет результат."""
    
    if not os.path.exists(input_path):
        print(f"[!] Файла {input_path} нет. Искать за тебя не буду.")
        return

    # 1. Читаем исходник
    with open(input_path, 'r', encoding='utf-8') as file:
        poem_text = file.read()
        
    print(f"[*] Считано {len(poem_text)} символов. Запускаем мясорубку...")

    # 2. Упрощаем
    simplified_poem = simplify_text(
        text=poem_text,
        target_complexity=target_complexity,
        freq_dict=freq_dict,
        max_workers=10
    )

    # 3. Сохраняем
    with open(output_path, 'w', encoding='utf-8') as file:
        file.write(simplified_poem)
        
    print(f"[*] Готово. Результат слит в {output_path}")

# Пример запуска:
if __name__ == "__main__":
    FREQ_DICT = parse_pdf_freq_dict('chastota.pdf') # Загружай словарь здесь
    
    input_file = "gabdulla_poem.txt"
    output_file = "gabdulla_simple.txt"
    
    simplify_poem_from_file(input_file, output_file, target_complexity=0.6, freq_dict=FREQ_DICT)
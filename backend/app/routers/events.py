from datetime import datetime
import json
import re
import sqlite3
import urllib.parse
import urllib.request

from fastapi import APIRouter
from pydantic import BaseModel

from app.database import DB_PATH


router = APIRouter(prefix="/events", tags=["Events"])


TRANSLATIONS = {
    "мин": "я",
    "син": "ты",
    "ул": "он / она",
    "без": "мы",
    "сез": "вы",
    "алар": "они",

    "әни": "мама",
    "әти": "папа",
    "ана": "мать",
    "әби": "бабушка",
    "бабай": "дедушка",
    "малай": "мальчик / сын",
    "кыз": "девочка / дочь",
    "бала": "ребёнок",

    "су": "вода",
    "урман": "лес",
    "елга": "река",
    "күл": "озеро",
    "тау": "гора",
    "җир": "земля",
    "күк": "небо",
    "кояш": "солнце",
    "ай": "луна / месяц",
    "йолдыз": "звезда",
    "җил": "ветер",
    "яңгыр": "дождь",
    "кар": "снег",

    "йорт": "дом",
    "өй": "дом",
    "мәктәп": "школа",
    "шәһәр": "город",
    "авыл": "деревня",
    "урам": "улица",

    "көн": "день",
    "төн": "ночь",
    "иртә": "утро",
    "кичә": "вчера",
    "бүген": "сегодня",
    "иртәгә": "завтра",

    "бар": "иди / есть",
    "бару": "идти",
    "кил": "приходи",
    "килү": "приходить",
    "күр": "смотри / видеть",
    "күрү": "видеть",
    "әйт": "скажи",
    "әйтү": "говорить",
    "укы": "читай",
    "уку": "читать",
    "яз": "пиши",
    "язу": "писать",
    "ал": "бери",
    "алу": "брать",
    "бир": "дай",
    "бирү": "давать",
    "аша": "ешь",
    "ашау": "есть",
    "эч": "пей",
    "эчү": "пить",
    "ярат": "люби",
    "ярату": "любить",

    "матур": "красивый",
    "зур": "большой",
    "кечкенә": "маленький",
    "яхшы": "хороший",
    "начар": "плохой",
    "озын": "длинный",
    "кыска": "короткий",
    "яңа": "новый",
    "иске": "старый",

    "ак": "белый",
    "кара": "чёрный",
    "кызыл": "красный",

    "әкият": "сказка",
    "шүрәле": "Шурале, лесной дух",
    "дус": "друг",
    "дуслык": "дружба",
    "кеше": "человек",
    "егет": "юноша",
    "патша": "царь",
    "хан": "хан",

    "китап": "книга",
    "сүз": "слово",
    "тел": "язык",
    "җыр": "песня",
    "вакыт": "время",
    "эш": "работа / дело",
    "күз": "глаз",
    "йөрәк": "сердце",
}


def clean_word(word: str) -> str:
    word = word.strip().lower()

    word = re.sub(
        r"^[^\wәғқңөһүҗӘҒҚҢӨҺҮҖА-Яа-яЁё-]+",
        "",
        word,
        flags=re.UNICODE
    )

    word = re.sub(
        r"[^\wәғқңөһүҗӘҒҚҢӨҺҮҖА-Яа-яЁё-]+$",
        "",
        word,
        flags=re.UNICODE
    )

    return word


def google_translate(text: str) -> str | None:

    if not text or not text.strip():
        return None

    try:
        encoded_text = urllib.parse.quote(text)

        url = (
            "https://translate.googleapis.com/translate_a/single"
            "?client=gtx"
            "&sl=tt"
            "&tl=ru"
            "&dt=t"
            f"&q={encoded_text}"
        )

        request = urllib.request.Request(
            url,
            headers={
                "User-Agent": "Mozilla/5.0"
            }
        )

        with urllib.request.urlopen(
            request,
            timeout=10
        ) as response:

            raw_data = response.read().decode("utf-8")

        data = json.loads(raw_data)

        if not data or not data[0]:
            return None

        result = ""

        for item in data[0]:

            if item and len(item) > 0 and item[0]:
                result += item[0]

        result = result.strip()

        return result if result else None

    except Exception as error:

        print(
            f"Translation API error: {error}"
        )

        return None


# =========================================================
# EVENT MODEL
# =========================================================

class WordEvent(BaseModel):

    book_id: int

    word: str

    event_type: str = "translation"

    user_id: str = "demo_user"


# =========================================================
# CREATE / UPDATE EVENTS TABLE
# =========================================================

def ensure_events_table():

    connection = sqlite3.connect(DB_PATH)

    try:

        cursor = connection.cursor()

        cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS word_events (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                book_id INTEGER NOT NULL,
                word TEXT NOT NULL,
                event_type TEXT NOT NULL,
                user_id TEXT NOT NULL DEFAULT 'demo_user',
                created_at TEXT NOT NULL
            )
            """
        )

        # Если старая таблица уже существует,
        # добавляем user_id.
        cursor.execute(
            "PRAGMA table_info(word_events)"
        )

        columns = [
            row[1]
            for row in cursor.fetchall()
        ]

        if "user_id" not in columns:

            cursor.execute(
                """
                ALTER TABLE word_events
                ADD COLUMN user_id TEXT
                """
            )

            cursor.execute(
                """
                UPDATE word_events
                SET user_id = 'demo_user'
                WHERE user_id IS NULL
                """
            )

        connection.commit()

    finally:

        connection.close()


# =========================================================
# WORD EVENT
# =========================================================

@router.post("/word")
def create_word_event(event: WordEvent):

    ensure_events_table()

    connection = sqlite3.connect(DB_PATH)

    try:

        cursor = connection.cursor()

        cursor.execute(
            """
            INSERT INTO word_events (
                book_id,
                word,
                event_type,
                user_id,
                created_at
            )
            VALUES (?, ?, ?, ?, ?)
            """,
            (
                event.book_id,
                event.word,
                event.event_type,
                event.user_id,
                datetime.utcnow().isoformat()
            )
        )

        connection.commit()

        return {
            "status": "ok",
            "book_id": event.book_id,
            "word": event.word,
            "event_type": event.event_type,
            "user_id": event.user_id
        }

    finally:

        connection.close()


# =========================================================
# WORD TRANSLATION
# =========================================================

@router.get("/word")
def get_word_translation(word: str):

    original_word = word

    clean = clean_word(word)

    if not clean:

        return {
            "word": original_word,
            "translation": "Пустое слово"
        }

    translation = google_translate(clean)

    if not translation:

        translation = TRANSLATIONS.get(clean)

    if not translation:

        translation = "Перевод не найден"

    return {
        "word": original_word,
        "translation": translation
    }


# =========================================================
# PHRASE — ОСТАВЛЯЕМ API, НО НЕ ИСПОЛЬЗУЕМ В UI
# =========================================================

@router.get("/phrase")
def get_phrase_translation(phrase: str):

    phrase = phrase.strip()

    if not phrase:

        return {
            "phrase": "",
            "translation": "Пустая фраза"
        }

    translation = google_translate(phrase)

    if not translation:

        words = phrase.split()

        translated_words = []

        for word in words:

            clean = clean_word(word)

            translated_words.append(
                TRANSLATIONS.get(
                    clean,
                    word
                )
            )

        translation = " ".join(
            translated_words
        )

    return {
        "phrase": phrase,
        "translation": translation
    }


# =========================================================
# STATISTICS
# =========================================================

@router.get("/stats")
def get_translation_stats(
    user_id: str = "demo_user"
):

    ensure_events_table()

    connection = sqlite3.connect(DB_PATH)

    try:

        cursor = connection.cursor()

        # Общее количество переводов
        cursor.execute(
            """
            SELECT COUNT(*)
            FROM word_events
            WHERE user_id = ?
            AND event_type = 'translation'
            """,
            (user_id,)
        )

        total_translations = cursor.fetchone()[0]


        # Количество уникальных слов
        cursor.execute(
            """
            SELECT COUNT(DISTINCT LOWER(word))
            FROM word_events
            WHERE user_id = ?
            AND event_type = 'translation'
            """,
            (user_id,)
        )

        unique_words = cursor.fetchone()[0]


        # Последние переведённые слова
        cursor.execute(
            """
            SELECT
                word,
                created_at
            FROM word_events
            WHERE user_id = ?
            AND event_type = 'translation'
            ORDER BY id DESC
            LIMIT 20
            """,
            (user_id,)
        )

        recent_rows = cursor.fetchall()

        recent_words = [

            {
                "word": row[0],
                "created_at": row[1]
            }

            for row in recent_rows
        ]


        # Самые часто переводимые слова
        cursor.execute(
            """
            SELECT
                LOWER(word) AS word,
                COUNT(*) AS count
            FROM word_events
            WHERE user_id = ?
            AND event_type = 'translation'
            GROUP BY LOWER(word)
            ORDER BY count DESC
            LIMIT 10
            """,
            (user_id,)
        )

        popular_rows = cursor.fetchall()

        popular_words = [

            {
                "word": row[0],
                "count": row[1]
            }

            for row in popular_rows
        ]


        return {

            "user_id": user_id,

            "total_translations":
                total_translations,

            "unique_words":
                unique_words,

            "recent_words":
                recent_words,

            "popular_words":
                popular_words
        }

    finally:

        connection.close()
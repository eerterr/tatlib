import os
import re
import sqlite3
import tempfile
import json
import urllib.parse
import urllib.request
from pathlib import Path

import cv2
import pytesseract

from fastapi import APIRouter, File, HTTPException, UploadFile
from pydantic import BaseModel


router = APIRouter(
    prefix="/ocr",
    tags=["OCR"]
)


# =========================================================
# PATHS
# =========================================================

BASE_DIR = Path(__file__).resolve().parents[2]

DB_PATH = BASE_DIR / "tatar_adaptive.db"

TESSERACT_PATH = r"C:\Program Files\Tesseract-OCR\tesseract.exe"
TAT_MODEL = r"C:\Users\Aigul\Downloads\tat_cyrl.traineddata"


# =========================================================
# TESSERACT SETUP
# =========================================================

pytesseract.pytesseract.tesseract_cmd = TESSERACT_PATH

os.environ["TESSDATA_PREFIX"] = r"C:\Users\Aigul\Downloads"


# =========================================================
# RESPONSE MODELS
# =========================================================

class OcrResponse(BaseModel):
    text: str
    book_found: bool
    book_id: int | None = None
    book_title: str | None = None
    book_author: str | None = None
    confidence: float | None = None


class OcrTranslateRequest(BaseModel):
    text: str


class OcrTranslateResponse(BaseModel):
    translation: str


# =========================================================
# NORMALIZE
# =========================================================

def normalize_text(text: str) -> str:

    text = text.lower()

    text = text.replace("\n", " ")
    text = text.replace("\r", " ")

    text = re.sub(
        r"\s+",
        " ",
        text
    )

    return text.strip()


def get_words(text: str) -> set[str]:

    text = normalize_text(text)

    found = re.findall(
        r"[а-яёәөүҗңһА-ЯЁӘӨҮҖҢҺ]+",
        text
    )

    return {
        word.lower()
        for word in found
        if len(word) >= 3
    }


# =========================================================
# IMAGE PREPROCESSING
# =========================================================

def preprocess_image(image):

    if image is None:
        raise ValueError(
            "Не удалось прочитать изображение"
        )

    gray = cv2.cvtColor(
        image,
        cv2.COLOR_BGR2GRAY
    )

    gray = cv2.resize(
        gray,
        None,
        fx=2,
        fy=2,
        interpolation=cv2.INTER_CUBIC
    )

    blurred = cv2.GaussianBlur(
        gray,
        (5, 5),
        0
    )

    _, threshold = cv2.threshold(
        blurred,
        0,
        255,
        cv2.THRESH_BINARY + cv2.THRESH_OTSU
    )

    return threshold


# =========================================================
# OCR
# =========================================================

def recognize_text(image):

    processed = preprocess_image(image)

    config = "--psm 3"

    text = pytesseract.image_to_string(
        processed,
        lang="tat_cyrl",
        config=config
    )

    try:

        data = pytesseract.image_to_data(
            processed,
            lang="tat_cyrl",
            config=config,
            output_type=pytesseract.Output.DICT
        )

        confidences = []

        for value in data["conf"]:

            try:

                value = float(value)

                if value >= 0:
                    confidences.append(value)

            except (ValueError, TypeError):
                pass

        if confidences:

            confidence = (
                sum(confidences)
                / len(confidences)
                / 100
            )

        else:
            confidence = 0.0

    except Exception:

        confidence = 0.0

    return text.strip(), confidence


# =========================================================
# DATABASE
# =========================================================

def get_connection():

    if not DB_PATH.exists():

        raise FileNotFoundError(
            f"База данных не найдена: {DB_PATH}"
        )

    connection = sqlite3.connect(
        str(DB_PATH)
    )

    connection.row_factory = sqlite3.Row

    return connection


# =========================================================
# FIND BOOK
# =========================================================

def find_book(recognized_text: str):

    recognized_words = get_words(
        recognized_text
    )

    if not recognized_words:
        return None

    connection = get_connection()

    try:

        books = connection.execute(
            """
            SELECT
                id,
                title,
                author
            FROM books
            ORDER BY id
            """
        ).fetchall()

        best_book = None
        best_score = 0.0

        for book in books:

            # В БД нет block_index.
            # Используем существующий id.
            blocks = connection.execute(
                """
                SELECT text
                FROM text_blocks
                WHERE book_id = ?
                ORDER BY id
                """,
                (book["id"],)
            ).fetchall()

            original_text = " ".join(
                row["text"] or ""
                for row in blocks
            )

            book_words = get_words(
                original_text
            )

            if not book_words:
                continue

            common_words = (
                recognized_words &
                book_words
            )

            if not common_words:
                continue

            score = (
                len(common_words)
                / max(
                    len(recognized_words),
                    1
                )
            )

            if score > best_score:

                best_score = score

                best_book = {
                    "id": book["id"],
                    "title": book["title"],
                    "author": book["author"],
                    "score": score
                }

        if (
            best_book is not None
            and best_score >= 0.30
        ):

            return best_book

        return None

    finally:

        connection.close()


# =========================================================
# FULL TEXT TRANSLATION
# =========================================================

def translate_text(text: str) -> str:

    if not text.strip():
        return ""

    # Google Translate API endpoint без дополнительной библиотеки.
    url = "https://translate.googleapis.com/translate_a/single"

    # Разбиваем длинный OCR-текст на части.
    chunks = []

    current = ""

    for sentence in re.split(
        r"(?<=[.!?…])\s+",
        text
    ):

        if len(current) + len(sentence) > 2500:

            if current.strip():
                chunks.append(current.strip())

            current = sentence

        else:

            if current:
                current += " " + sentence
            else:
                current = sentence

    if current.strip():
        chunks.append(current.strip())

    translated_chunks = []

    for chunk in chunks:

        params = {
            "client": "gtx",
            "sl": "tt",
            "tl": "ru",
            "dt": "t",
            "q": chunk
        }

        request_url = (
            url
            + "?"
            + urllib.parse.urlencode(params)
        )

        request = urllib.request.Request(
            request_url,
            headers={
                "User-Agent": "Mozilla/5.0"
            }
        )

        with urllib.request.urlopen(
            request,
            timeout=20
        ) as response:

            data = json.loads(
                response.read().decode(
                    "utf-8"
                )
            )

        result = ""

        if (
            isinstance(data, list)
            and len(data) > 0
            and isinstance(data[0], list)
        ):

            for item in data[0]:

                if (
                    isinstance(item, list)
                    and len(item) > 0
                    and item[0]
                ):

                    result += str(item[0])

        if result.strip():
            translated_chunks.append(
                result.strip()
            )

    if not translated_chunks:
        raise RuntimeError(
            "Не удалось получить перевод текста"
        )

    return "\n\n".join(
        translated_chunks
    )


# =========================================================
# OCR ENDPOINT
# =========================================================

@router.post(
    "",
    response_model=OcrResponse
)
async def recognize_image(
    file: UploadFile = File(...)
):

    if file is None:

        raise HTTPException(
            status_code=400,
            detail="Файл не передан"
        )

    try:

        contents = await file.read()

    except Exception as e:

        raise HTTPException(
            status_code=400,
            detail=f"Не удалось прочитать файл: {e}"
        )

    if not contents:

        raise HTTPException(
            status_code=400,
            detail="Файл пустой"
        )

    temp_path = None

    try:

        suffix = (
            Path(file.filename).suffix
            if file.filename
            else ".jpg"
        )

        with tempfile.NamedTemporaryFile(
            delete=False,
            suffix=suffix
        ) as temp_file:

            temp_file.write(contents)

            temp_path = temp_file.name

        image = cv2.imread(
            temp_path
        )

        if image is None:

            raise HTTPException(
                status_code=400,
                detail="Не удалось прочитать изображение"
            )

        try:

            text, confidence = recognize_text(
                image
            )

        except Exception as e:

            raise HTTPException(
                status_code=500,
                detail=f"Ошибка Tesseract: {e}"
            )

        if not text.strip():

            return OcrResponse(
                text="",
                book_found=False,
                confidence=confidence
            )

        try:

            book = find_book(
                text
            )

        except Exception as e:

            raise HTTPException(
                status_code=500,
                detail=f"Ошибка поиска книги: {e}"
            )

        if book:

            return OcrResponse(
                text=text,
                book_found=True,
                book_id=book["id"],
                book_title=book["title"],
                book_author=book["author"],
                confidence=confidence
            )

        return OcrResponse(
            text=text,
            book_found=False,
            confidence=confidence
        )

    finally:

        if temp_path:

            try:
                os.remove(temp_path)

            except OSError:
                pass


# =========================================================
# TRANSLATE OCR TEXT
# =========================================================

@router.post(
    "/translate",
    response_model=OcrTranslateResponse
)
def translate_ocr_text(
    request: OcrTranslateRequest
):

    if not request.text.strip():

        raise HTTPException(
            status_code=400,
            detail="Текст для перевода пустой"
        )

    try:

        translation = translate_text(
            request.text
        )

        return OcrTranslateResponse(
            translation=translation
        )

    except Exception as e:

        raise HTTPException(
            status_code=500,
            detail=f"Ошибка перевода: {e}"
        )
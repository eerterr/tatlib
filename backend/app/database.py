from pathlib import Path
import sqlite3


BASE_DIR = Path(__file__).resolve().parent.parent
DB_PATH = BASE_DIR / "tatar_adaptive.db"


def get_connection():
    connection = sqlite3.connect(DB_PATH)
    connection.row_factory = sqlite3.Row
    return connection


def get_books():
    connection = get_connection()

    try:
        cursor = connection.cursor()

        cursor.execute("""
            SELECT
                b.id,
                b.title,
                b.author,
                b.genre,
                b.year,
                b.russian_title,
                b.aliases
            FROM books b
            ORDER BY b.id
        """)

        return [dict(row) for row in cursor.fetchall()]

    finally:
        connection.close()


def get_book(book_id: int):
    connection = get_connection()

    try:
        cursor = connection.cursor()

        # Основная информация о книге
        cursor.execute("""
            SELECT
                b.id,
                b.title,
                b.author,
                b.genre,
                b.year,
                b.russian_title,
                b.aliases
            FROM books b
            WHERE b.id = ?
        """, (book_id,))

        book_row = cursor.fetchone()

        if book_row is None:
            return None

        book = dict(book_row)

        # Текстовые блоки книги
        cursor.execute("""
            SELECT
                id,
                block_order,
                text,
                adapted_text,
                russian_text
            FROM text_blocks
            WHERE book_id = ?
            ORDER BY block_order
        """, (book_id,))

        blocks = [
            dict(row)
            for row in cursor.fetchall()
        ]

        book["blocks"] = blocks

        return book

    finally:
        connection.close()
package com.tatlib.app.data

/**
 * Данные книг, которых `/api/books` не отдаёт: уровень (`text_complexity.estimated_level`),
 * год (`books.year`) и цифры `book_stats` (word_count / unique_words / text_blocks_count).
 * Скопировано из `backend/tatar_adaptive.db` 20.09.2026 без изменений; подсчёт слов на клиенте
 * расходится с базой на 1 у книг 2 и 4, поэтому считать нельзя — только копия.
 */
// TODO backend: отдавать estimated_level, year и book_stats в /api/books, после чего удалить этот файл.
data class BookMeta(
    val level: TatarLevel,
    /** null — в базе года нет (показывать «—»). */
    val year: Int?,
    val wordCount: Int,
    val uniqueWords: Int,
    val blocks: Int
)

object BookMetaTable {
    private val byId = mapOf(
        "1" to BookMeta(TatarLevel.B1, 1908, 413, 292, 28),   // Су анасы
        "2" to BookMeta(TatarLevel.B1, 1907, 929, 623, 4),    // Шүрәле
        "3" to BookMeta(TatarLevel.B1, null, 1560, 731, 32),  // Нәҗип
        "4" to BookMeta(TatarLevel.B2, 1908, 1500, 845, 29)   // Алтын әтәч
    )

    fun of(bookId: String): BookMeta? = byId[bookId]
}

/** Уровень книги для интерфейса: из таблицы, пока API его не отдаёт; иначе — как в модели. */
val Book.displayLevel: TatarLevel get() = BookMetaTable.of(id)?.level ?: level

/** Год для интерфейса: «—», если в базе пусто. */
val Book.displayYear: String get() = BookMetaTable.of(id)?.year?.toString() ?: year.ifBlank { "—" }

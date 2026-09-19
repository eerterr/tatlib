package com.tatlib.app.data

/** CEFR-style reading level used across the app to badge books and the user's own level. */
enum class TatarLevel(val label: String) {
    A1("A1"), A2("A2"), B1("B1"), B2("B2"), C1("C1")
}

data class GlossWord(
    val word: String,
    val translationRu: String
)

data class BookPage(
    val bodyText: String,
    /** Words on this page that should be tappable, with their translation. */
    val glossary: List<GlossWord> = emptyList(),
    /** A short phrase from this page highlighted as the "word of the page" card. */
    val highlightPhrase: String? = null
)

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val genre: String,
    val year: String,
    val level: TatarLevel,
    val tags: List<String>,
    val description: String,
    val readingTimeLabel: String,
    val pages: List<BookPage>,
    /** 0f..1f, how far the mock "current user" has already read into this book. */
    val progress: Float = 0f
) {
    val pageCount: Int get() = pages.size
}

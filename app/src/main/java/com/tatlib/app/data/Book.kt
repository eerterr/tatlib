package com.tatlib.app.data

/** CEFR-style reading level used across the app. */
enum class TatarLevel(val label: String) {
    A1("A1"),
    A2("A2"),
    B1("B1"),
    B2("B2"),
    C1("C1")
}

data class GlossWord(
    val word: String,
    val translationRu: String
)

data class BookPage(
    /** Original Tatar text. */
    val bodyText: String,

    /** Words on this page that can be tapped. */
    val glossary: List<GlossWord> = emptyList(),

    /** Short highlighted phrase. */
    val highlightPhrase: String? = null,

    /** 100% difficulty — original Tatar text. */
    val originalText: String = bodyText,

    /** 50% difficulty — adapted Tatar text. */
    val adaptedText: String = "",

    /** 0% difficulty — Russian translation. */
    val russianText: String = ""
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
    /** 0f..1f — current reading progress. */
    val progress: Float = 0f
) {
    val pageCount: Int
        get() = pages.size
}
package com.tatlib.app.data

/**
 * Мок теста уровня для кликабельного прототипа (API теста нет).
 * Мок книг, статистики и цитат удалён в шаге 4.3: книги идут из `/api/books`,
 * цифры — из `BookMetaTable` и ответов API.
 */
object MockData {

    // -------------------------------------------------------------------
    // Level-assessment quiz
    // -------------------------------------------------------------------

    data class QuizOption(val text: String, val isCorrect: Boolean)
    data class QuizQuestion(val prompt: String, val options: List<QuizOption>)

    val levelQuiz = listOf(
        QuizQuestion(
            prompt = "Как правильно сказать «Я читаю книгу»?",
            options = listOf(
                QuizOption("Мин китап укыйм", true),
                QuizOption("Мин китап укыдым", false),
                QuizOption("Мин китап укыячакмын", false),
                QuizOption("Мин китап укымыйм", false)
            )
        ),
        QuizQuestion(
            prompt = "Как будет «Я прочитал книгу» (прошедшее время)?",
            options = listOf(
                QuizOption("Мин китап укыйм", false),
                QuizOption("Мин китап укыдым", true),
                QuizOption("Мин китап укыячакмын", false),
                QuizOption("Мин китап укымыйм", false)
            )
        ),
        QuizQuestion(
            prompt = "Что значит «Рәхим итегез!»?",
            options = listOf(
                QuizOption("Добро пожаловать!", true),
                QuizOption("До свидания", false),
                QuizOption("Спасибо", false),
                QuizOption("Извините", false)
            )
        ),
        QuizQuestion(
            prompt = "Что означает слово «дәрәҗә»?",
            options = listOf(
                QuizOption("уровень", true),
                QuizOption("слово", false),
                QuizOption("книга", false),
                QuizOption("время", false)
            )
        ),
        QuizQuestion(
            prompt = "Выберите синоним к слову «алга»",
            options = listOf(
                QuizOption("вперёд", true),
                QuizOption("назад", false),
                QuizOption("быстро", false),
                QuizOption("медленно", false)
            )
        )
    )

    /** Very small mock scoring rule — good enough for a clickable prototype. */
    fun levelFromScore(correctAnswers: Int): TatarLevel = when (correctAnswers) {
        0, 1 -> TatarLevel.A1
        2 -> TatarLevel.A2
        3 -> TatarLevel.B1
        4 -> TatarLevel.B2
        else -> TatarLevel.C1
    }
}

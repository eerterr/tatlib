package com.tatlib.app.ui.navigation

object Routes {
    const val SPLASH = "splash"

    const val ONBOARDING_WELCOME = "onboarding_welcome"
    const val AUTH_CHOICE = "auth_choice"
    const val REGISTER = "register"
    const val LOGIN = "login"

    const val LEVEL_INTRO = "level_intro"
    const val LEVEL_QUIZ = "level_quiz"
    /** Результат теста: `score` — число верных ответов (нет → уровень по умолчанию B1). */
    const val LEVEL_RESULT = "level_result?score={score}"
    const val LEVEL_RESULT_SCORE_ARG = "score"

    const val LIBRARY = "library"
    const val SEARCH = "search"
    const val PROGRESS = "progress"
    const val SCANNER = "scanner"
    /** Профиль и настройки — вход через аватар на «Алгарышың» (PROMPT-PHASE-4 § 2.7). */
    const val PROFILE = "profile"

    const val BOOK_DETAIL = "book_detail/{bookId}"
    const val BOOK_READER = "book_reader/{bookId}"
    const val RECAP = "recap/{bookId}"

    fun levelResult(score: Int? = null) = if (score == null) "level_result" else "level_result?score=$score"
    fun bookDetail(bookId: String) = "book_detail/$bookId"
    fun bookReader(bookId: String) = "book_reader/$bookId"
    fun recap(bookId: String) = "recap/$bookId"

    /** Routes that show the bottom navigation bar. */
    val bottomBarRoutes = setOf(LIBRARY, SEARCH, SCANNER, PROGRESS)

    /** Routes that show the «Хәзер укыла» mini bar above the bottom navigation. */
    val miniBarRoutes = setOf(LIBRARY, SEARCH)
}

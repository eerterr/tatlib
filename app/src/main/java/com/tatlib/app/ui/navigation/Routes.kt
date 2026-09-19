package com.tatlib.app.ui.navigation

object Routes {
    const val SPLASH = "splash"

    const val ONBOARDING_WELCOME = "onboarding_welcome"
    const val AUTH_CHOICE = "auth_choice"
    const val REGISTER = "register"
    const val LOGIN = "login"

    const val LEVEL_INTRO = "level_intro"
    const val LEVEL_QUIZ = "level_quiz"
    const val LEVEL_RESULT = "level_result"

    const val LIBRARY = "library"
    const val SEARCH = "search"
    const val PROGRESS = "progress"
    const val SCANNER = "scanner"

    const val BOOK_DETAIL = "book_detail/{bookId}"
    const val BOOK_READER = "book_reader/{bookId}"
    const val RECAP = "recap/{bookId}"

    fun bookDetail(bookId: String) = "book_detail/$bookId"
    fun bookReader(bookId: String) = "book_reader/$bookId"
    fun recap(bookId: String) = "recap/$bookId"

    /** Routes that show the bottom navigation bar. */
    val bottomBarRoutes = setOf(LIBRARY, SEARCH, PROGRESS)
}

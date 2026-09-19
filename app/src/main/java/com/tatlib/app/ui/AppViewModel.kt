package com.tatlib.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tatlib.app.data.Book
import com.tatlib.app.data.MockData
import com.tatlib.app.data.TatarLevel

enum class AppLanguage { TAT, RU }

/**
 * Single shared in-memory "session" for the whole prototype.
 *
 * There is intentionally no persistence, no auth backend and no ML/ASR wiring here —
 * per the brief this is a clickable mock: state resets when the process dies, and every
 * field below is exactly what a real UserRepository / AuthRepository / BookRepository
 * should replace later.
 */
class AppViewModel : ViewModel() {

    // Cosmetic TAT/RU toggle shown in the top bar of most screens.
    var language by mutableStateOf(AppLanguage.TAT)
        private set

    fun toggleLanguage() {
        language = if (language == AppLanguage.TAT) AppLanguage.RU else AppLanguage.TAT
    }

    // Auth (mock) ---------------------------------------------------------
    var userName by mutableStateOf("Айгуль")
        private set

    var selectedAgeBracket by mutableStateOf<String?>(null)
    var registeredOrLoggedIn by mutableStateOf(false)
        private set

    fun completeRegistration(name: String) {
        if (name.isNotBlank()) userName = name
        registeredOrLoggedIn = true
    }

    fun completeLogin() {
        registeredOrLoggedIn = true
    }

    // Level test (mock) ----------------------------------------------------
    var quizAnswers = mutableStateOf<Map<Int, Boolean>>(emptyMap())
        private set

    fun recordQuizAnswer(questionIndex: Int, correct: Boolean) {
        quizAnswers.value = quizAnswers.value + (questionIndex to correct)
    }

    fun resetQuiz() {
        quizAnswers.value = emptyMap()
    }

    val quizScore: Int get() = quizAnswers.value.values.count { it }

    var userLevel by mutableStateOf<TatarLevel?>(null)
        private set

    fun finishQuizAndComputeLevel(): TatarLevel {
        val level = MockData.levelFromScore(quizScore)
        userLevel = level
        return level
    }

    fun setLevelManually(level: TatarLevel) {
        userLevel = level
    }

    // Library / reading progress (mock) ------------------------------------
    private val readingProgress = mutableStateOf(
        MockData.allBooks.associate { it.id to it.progress }.toMutableMap()
    )

    var continueReadingBookId by mutableStateOf(MockData.shurale.id)
        private set

    fun progressFor(book: Book): Float = readingProgress.value[book.id] ?: 0f

    fun updateProgress(bookId: String, progress: Float) {
        readingProgress.value = readingProgress.value.toMutableMap().apply {
            put(bookId, progress.coerceIn(0f, 1f))
        }
        continueReadingBookId = bookId
    }
}

package com.tatlib.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tatlib.app.data.ApiClient
import com.tatlib.app.data.Book
import com.tatlib.app.data.BookPage
import com.tatlib.app.data.TatarLevel
import com.tatlib.app.data.WordEventDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    // =========================================================
    // BOOKS
    // =========================================================

    private val _books = MutableStateFlow<List<Book>>(emptyList())

    val books: StateFlow<List<Book>> =
        _books.asStateFlow()


    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook.asStateFlow()

    // =========================================================
    // LOADING / ERROR
    // =========================================================

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // =========================================================
    // WORD TRANSLATION
    // =========================================================

    private val _selectedWord = MutableStateFlow<String?>(null)
    val selectedWord: StateFlow<String?> = _selectedWord.asStateFlow()

    private val _selectedWordTranslation =
        MutableStateFlow<String?>(null)

    val selectedWordTranslation: StateFlow<String?> =
        _selectedWordTranslation.asStateFlow()

    private val _isLoadingTranslation =
        MutableStateFlow(false)

    val isLoadingTranslation: StateFlow<Boolean> =
        _isLoadingTranslation.asStateFlow()

    // =========================================================
    // TEXT VERSION
    // =========================================================

    /*
     * 0.0 = Russian
     * 0.5 = Adapted
     * 1.0 = Original
     */
    private val _selectedVersion =
        MutableStateFlow(1f)

    val selectedVersion: StateFlow<Float> =
        _selectedVersion.asStateFlow()

    // =========================================================
    // PROGRESS
    // =========================================================

    private val _progress =
        MutableStateFlow<Map<String, Float>>(emptyMap())

    val progress: StateFlow<Map<String, Float>> =
        _progress.asStateFlow()

    // =========================================================
    // INIT
    // =========================================================

    init {
        loadBooks()
    }

    // =========================================================
    // LOAD BOOKS
    // =========================================================

    fun loadBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = ApiClient.apiService.getBooks()

                _books.value = result.map { book ->

                    Book(
                        id = book.id.toString(),
                        title = book.title,
                        author = book.author,
                        genre = book.genre ?: "",
                        year = "",
                        level = parseLevel(book.level),
                        tags = emptyList(),
                        description = "",
                        readingTimeLabel = "",
                        pages = emptyList(),
                        progress =
                            _progress.value[book.id.toString()] ?: 0f
                    )
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Не удалось загрузить книги"

            } finally {
                _isLoading.value = false
            }
        }
    }

    // =========================================================
    // LOAD ONE BOOK
    // =========================================================

    fun loadBook(bookId: Int) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                val dto =
                    ApiClient.apiService.getBook(bookId)

                /*
                 * Один page = 4 text blocks.
                 *
                 * В реальном TextBlockDto поле оригинального
                 * текста называется `text`.
                 */
                val pages = dto.blocks
                    .chunked(4)
                    .map { blockGroup ->

                        val originalText =
                            blockGroup.joinToString("\n\n") {
                                it.text
                            }

                        val adaptedText =
                            blockGroup.joinToString("\n\n") {
                                it.adapted_text ?: ""
                            }

                        val russianText =
                            blockGroup.joinToString("\n\n") {
                                it.russian_text ?: ""
                            }

                        BookPage(
                            bodyText = originalText,
                            originalText = originalText,
                            adaptedText = adaptedText,
                            russianText = russianText,
                            glossary = emptyList()
                        )
                    }

                /*
                 * BookDetailDto содержит только:
                 *
                 * id
                 * title
                 * author
                 * blocks
                 *
                 * Поэтому genre/year/level здесь берём
                 * из уже загруженного списка книг.
                 */
                val existingBook =
                    _books.value.firstOrNull {
                        it.id == dto.id.toString()
                    }

                val book = Book(
                    id = dto.id.toString(),
                    title = dto.title,
                    author = dto.author,
                    genre = existingBook?.genre ?: "",
                    year = existingBook?.year ?: "",
                    level = existingBook?.level ?: TatarLevel.B1,
                    tags = existingBook?.tags ?: emptyList(),
                    description = existingBook?.description ?: "",
                    readingTimeLabel =
                        existingBook?.readingTimeLabel ?: "",
                    pages = pages,
                    progress =
                        _progress.value[bookId.toString()] ?: 0f
                )

                _selectedBook.value = book

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Не удалось загрузить книгу"

            } finally {
                _isLoading.value = false
            }
        }
    }

    // =========================================================
    // TEXT VERSION
    // =========================================================

    fun selectVersion(value: Float) {

        _selectedVersion.value = when {

            value < 0.25f -> 0f

            value < 0.75f -> 0.5f

            else -> 1f
        }
    }

    // =========================================================
    // WORD TRANSLATION
    // =========================================================

    /*
     * Переводим только отдельные слова.
     *
     * Phrase translation специально не используется.
     */
    fun translateWord(
        bookId: String,
        word: String
    ) {

        val id = bookId.toIntOrNull()
            ?: return

        if (word.isBlank()) {
            return
        }

        _selectedWord.value = word
        _selectedWordTranslation.value = null
        _isLoadingTranslation.value = true

        viewModelScope.launch {

            try {

                // Получаем перевод слова
                val result =
                    ApiClient.apiService
                        .getWordTranslation(word)

                _selectedWordTranslation.value =
                    result.translation

                // Сохраняем событие для статистики
                ApiClient.apiService.sendWordEvent(

                    WordEventDto(
                        book_id = id,
                        word = word,
                        event_type = "translation"
                    )
                )

            } catch (e: Exception) {

                // null → UI показывает ресурс reader_translation_missing (tt/ru)
                _selectedWordTranslation.value = null

            } finally {

                _isLoadingTranslation.value = false
            }
        }
    }

    // =========================================================
    // CLEAR WORD
    // =========================================================

    fun clearSelectedWord() {

        _selectedWord.value = null

        _selectedWordTranslation.value = null

        _isLoadingTranslation.value = false
    }

    // =========================================================
    // PROGRESS
    // =========================================================

    fun updateProgress(
        bookId: String,
        progressValue: Float
    ) {

        val newProgress =
            progressValue.coerceIn(0f, 1f)

        _progress.value =
            _progress.value.toMutableMap().apply {

                this[bookId] = newProgress
            }

        // Обновляем открытую книгу
        _selectedBook.value?.let { book ->

            if (book.id == bookId) {

                _selectedBook.value =
                    book.copy(
                        progress = newProgress
                    )
            }
        }

        // Обновляем список книг
        _books.value =
            _books.value.map { book ->

                if (book.id == bookId) {

                    book.copy(
                        progress = newProgress
                    )

                } else {
                    book
                }
            }
    }

    // =========================================================
    // GET BOOK FROM LOCAL STATE
    // =========================================================

    fun getBook(bookId: String): Book? {

        return _books.value.firstOrNull {
            it.id == bookId
        }
    }

    // =========================================================
    // LEVEL
    // =========================================================

    private fun parseLevel(
        level: String?
    ): TatarLevel {

        return when (level?.uppercase()) {

            "A1" -> TatarLevel.A1

            "A2" -> TatarLevel.A2

            "B1" -> TatarLevel.B1

            "B2" -> TatarLevel.B2

            "C1" -> TatarLevel.C1

            else -> TatarLevel.B1
        }
    }
}
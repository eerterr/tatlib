package com.tatlib.app.data

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


// =========================================================
// BOOKS
// =========================================================

data class BookDto(
    val id: Int,
    val title: String,
    val author: String,
    val genre: String? = null,
    val level: String? = null
)


data class TextBlockDto(
    val id: Int,
    val book_id: Int,
    val text: String,
    val adapted_text: String? = null,
    val russian_text: String? = null
)


data class BookDetailDto(
    val id: Int,
    val title: String,
    val author: String,
    val blocks: List<TextBlockDto>
)


// =========================================================
// WORD TRANSLATION
// =========================================================

data class WordTranslationDto(
    val word: String,
    val translation: String
)


data class WordEventDto(
    val book_id: Int,
    val word: String,
    val event_type: String = "translation",
    val user_id: String = "demo_user"
)


data class WordEventResponse(
    val status: String? = null,
    val word: String? = null
)


// =========================================================
// OCR
// =========================================================

data class OcrResponse(
    val text: String,
    val book_found: Boolean,
    val book_id: Int?,
    val book_title: String?,
    val book_author: String?,
    val confidence: Double?
)


data class OcrTranslateRequest(
    val text: String
)


data class OcrTranslateResponse(
    val translation: String
)


// =========================================================
// STATISTICS
// =========================================================

data class RecentWordDto(
    val word: String,
    val translation: String? = null,
    val created_at: String? = null
)


data class PopularWordDto(
    val word: String,
    val count: Int
)


data class TranslationStatsDto(
    val user_id: String,
    val total_translations: Int,
    val unique_words: Int,
    val recent_words: List<RecentWordDto> = emptyList(),
    val popular_words: List<PopularWordDto> = emptyList()
)


// =========================================================
// API
// =========================================================

interface ApiService {

    // -------------------------
    // BOOKS
    // -------------------------

    @GET("api/books")
    suspend fun getBooks(): List<BookDto>


    @GET("api/books/")
    suspend fun getBooksWithSlash(): List<BookDto>


    @GET("api/books/{book_id}")
    suspend fun getBook(
        @retrofit2.http.Path("book_id") bookId: Int
    ): BookDetailDto


    // -------------------------
    // WORD TRANSLATION
    // -------------------------

    @GET("api/events/word")
    suspend fun getWordTranslation(
        @Query("word") word: String
    ): WordTranslationDto


    @POST("api/events/word")
    suspend fun sendWordEvent(
        @Body event: WordEventDto
    ): WordEventResponse


    // -------------------------
    // STATISTICS
    // -------------------------

    @GET("api/events/stats")
    suspend fun getTranslationStats(
        @Query("user_id") userId: String = "demo_user"
    ): TranslationStatsDto


    // -------------------------
    // OCR
    // -------------------------

    @Multipart
    @POST("api/ocr")
    suspend fun recognizeImage(
        @Part file: MultipartBody.Part
    ): OcrResponse


    // -------------------------
    // FULL OCR TRANSLATION
    // -------------------------

    @POST("api/ocr/translate")
    suspend fun translateOcrText(
        @Body request: OcrTranslateRequest
    ): OcrTranslateResponse
}
package com.tatlib.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.tatlib.app.R

// Шкала — MASTER.md § 3.1 с правками § 0 (v2): заголовки экранов Golos Text 600 (h0),
// Playfair — только курсивные татарские фразы, цифры и обложки, ридер — Literata.
// Все три семейства проверены fontTools на ә ө ү җ ң һ (шаг 4.1). Лицензии OFL — assets/licenses/.

val PlayfairDisplay = FontFamily(
    Font(R.font.playfair_display_regular, FontWeight.Normal),
    Font(R.font.playfair_display_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.playfair_display_semibold, FontWeight.SemiBold)
)

val GolosText = FontFamily(
    Font(R.font.golos_text_regular, FontWeight.Normal),
    Font(R.font.golos_text_medium, FontWeight.Medium),
    Font(R.font.golos_text_semibold, FontWeight.SemiBold)
)

val Literata = FontFamily(
    Font(R.font.literata_regular, FontWeight.Normal),
    Font(R.font.literata_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.literata_medium, FontWeight.Medium)
)

private fun golos(weight: FontWeight, size: Int, line: Int, tracking: Double = 0.0) = TextStyle(
    fontFamily = GolosText,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = tracking.sp
)

private fun playfair(weight: FontWeight, size: Int, line: Int, italic: Boolean = false) = TextStyle(
    fontFamily = PlayfairDisplay,
    fontWeight = weight,
    fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
    fontSize = size.sp,
    lineHeight = line.sp
)

val AppTypography = Typography(
    // h0 на главной: «Хәерле көн!» 40/44
    displayLarge = golos(FontWeight.SemiBold, 40, 44, -0.6),
    // Playfair Italic: «B1» в кольце уровня
    displayMedium = playfair(FontWeight.Normal, 34, 40, italic = true),
    // Playfair Italic: «Татарча күбрәк» на сплэше, фраза дня
    displaySmall = playfair(FontWeight.Normal, 22, 28, italic = true),
    // h0: заголовок экрана 36/40
    headlineLarge = golos(FontWeight.SemiBold, 36, 40, -0.6),
    // h0 уменьшенный: Quiz, BookDetail, Search, Progress, Scanner, Profile (в макетах 26–34)
    headlineMedium = golos(FontWeight.SemiBold, 30, 36, -0.5),
    // h2: цифры статистики (tabular), слово во всплывашке
    headlineSmall = playfair(FontWeight.SemiBold, 22, 28),
    titleLarge = golos(FontWeight.SemiBold, 20, 26),       // t1: заголовки секций
    titleMedium = golos(FontWeight.SemiBold, 16, 22),      // t2: карточки, названия книг
    titleSmall = golos(FontWeight.Medium, 14, 20),         // текст чипа фильтра
    bodyLarge = golos(FontWeight.Normal, 16, 24),          // b1
    bodyMedium = golos(FontWeight.Normal, 14, 20),         // b2
    bodySmall = golos(FontWeight.Normal, 12, 16, 0.2),     // b3: метаданные
    labelLarge = golos(FontWeight.SemiBold, 15, 20, 0.2),  // lbl: кнопки, TAT/RU
    labelMedium = golos(FontWeight.SemiBold, 13, 18, 0.2), // чип уровня, сегменты слоёв
    labelSmall = golos(FontWeight.Medium, 11, 14, 1.2)     // ov: оверлайны (прописные делает компонент через .uppercase())
)

/** Шрифт ридера — Literata по умолчанию, Golos Text по выбору в профиле. */
enum class ReaderFont { LITERATA, GOLOS }

/** Текст книги: 16–20 sp, интерлиньяж ×1.55 (MASTER § 3.1, pages/reader.md). */
@Immutable
data class ReaderTypography(
    val fontSize: TextUnit = 18.sp,
    val font: ReaderFont = ReaderFont.LITERATA
) {
    private val family: FontFamily get() = if (font == ReaderFont.LITERATA) Literata else GolosText

    val body: TextStyle
        get() = TextStyle(fontFamily = family, fontWeight = FontWeight.Normal, fontSize = fontSize, lineHeight = 1.55.em)

    val bodyItalic: TextStyle
        get() = body.copy(fontStyle = FontStyle.Italic)

    companion object {
        const val MIN_SP = 16
        const val MAX_SP = 20
    }
}

val LocalReaderTypography = staticCompositionLocalOf { ReaderTypography() }

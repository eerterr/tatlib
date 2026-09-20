package com.tatlib.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.tatlib.app.data.TatarLevel

// Значения — design-system/tatlib/MASTER.md § 2 + § 0 (v2/v2.1) и docs/design/mockups/tatlib.css.
// Контрасты посчитаны в фазе 2, здесь не пересчитываются. Экраны цвета берут только через
// MaterialTheme.colorScheme и MaterialTheme.tatlibColors (MASTER § 8).

// ---- светлая тема ----
val Cream = Color(0xFFF6F1E7)          // background
val Paper = Color(0xFFFFFCF5)          // surface
val Sand = Color(0xFFECE4D4)           // surfaceVariant
val SandDeep = Color(0xFFE1D6C0)       // surfaceContainerHighest, pressed
val Ink = Color(0xFF1D3128)            // onBackground / primary
val InkSoft = Color(0xFF5C6A61)        // onSurfaceVariant
val InkMuted = Color(0xFF58675F)       // outline (подписи, плейсхолдеры)
val Line = Color(0xFFD9CFBC)           // outlineVariant (разделители)
val Forest = Color(0xFF4E6B48)         // secondary (прогресс, иконки состояния)
val Sage = Color(0xFF8FAF80)           // secondaryContainer (заливки ≥ 24 dp, подчёркивание ссылок)
val SageContainer = Color(0xFFDCE8D2)  // primaryContainer
val Sage2 = Color(0xFFC9DDB1)          // пилюля «Укырга»
val Terracotta = Color(0xFFB4522E)     // tertiary (акценты, TAT/RU)
val TerracottaDeep = Color(0xFF9A4426)
val Sunset = Color(0xFFC9663F)
val Peach = Color(0xFFF1C1A8)          // tertiaryContainer
val Peach2 = Color(0xFFF3C7A1)         // круг основного действия
val PeachContainer = Color(0xFFF7E1D0)
val Sky = Color(0xFFA9C8E3)
val SkyContainer = Color(0xFFDCE9F4)
val Sky2 = Color(0xFFBFD7EA)           // круг второстепенного действия, дуги
val ErrorLight = Color(0xFFB3402A)
val White = Color(0xFFFFFFFF)

// ---- тёмная тема (отдельная схема, MASTER § 2.2 + tatlib.css .dark) ----
val Night = Color(0xFF14201B)
val NightPaper = Color(0xFF1B2823)
val NightSand = Color(0xFF243330)
val NightSandDeep = Color(0xFF2E3F39)
val CreamText = Color(0xFFF1EBDD)
val NightInkSoft = Color(0xFFB7C1B9)
val NightInkMuted = Color(0xFF8E9A92)
val NightLine = Color(0xFF3A4A43)
val SageLight = Color(0xFFA9C79A)      // primary / secondary / forest / sage2 в тёмной
val NightSageContainer = Color(0xFF2E4638)
val TerracottaLight = Color(0xFFE08A66)
val TerracottaLightDeep = Color(0xFFD07A56)
val NightPeach = Color(0xFF4A2A1E)     // tertiaryContainer / peach-c
val NightPeachText = Color(0xFFF5D3C0) // onTertiaryContainer
val NightPeach2 = Color(0xFFD9A57C)
val NightSky = Color(0xFF8FB6D8)
val NightSkyContainer = Color(0xFF243A48)
val NightSky2 = Color(0xFF7FA6C4)
val ErrorDark = Color(0xFFE08A78)

/** Пара контейнер/текст и точка для бейджа уровня (MASTER § 2.3). */
@Immutable
data class LevelColors(val container: Color, val text: Color, val dot: Color)

/** Расширение M3-схемы: то, чему в ColorScheme нет роли. */
@Immutable
data class TatlibColors(
    val terracotta: Color,
    val terracottaDeep: Color,
    val sunset: Color,
    val peach: Color,
    val peach2: Color,
    val peachContainer: Color,
    val sky: Color,
    val sky2: Color,
    val skyContainer: Color,
    val sage: Color,
    val sage2: Color,
    val sageContainer: Color,
    val forest: Color,
    val sandDeep: Color,
    val line: Color,
    val inkSoft: Color,
    val inkMuted: Color,
    /** Текст и белые элементы поверх фото (всегда белые, в обеих темах). */
    val onPhoto: Color,
    /** Стекло на фото: заливка 34 %, рамка 55 %, поле 55 % (MASTER § 0). */
    val glassFill: Color,
    val glassBorder: Color,
    val glassField: Color,
    /** Тень всплывашки и мини-бара: rgba(29,49,40,.16); в тёмной теме тени нет. */
    val shadow: Color,
    val levels: Map<TatarLevel, LevelColors>
) {
    fun level(level: TatarLevel): LevelColors = levels.getValue(level)
}

val LightTatlibColors = TatlibColors(
    terracotta = Terracotta,
    terracottaDeep = TerracottaDeep,
    sunset = Sunset,
    peach = Peach,
    peach2 = Peach2,
    peachContainer = PeachContainer,
    sky = Sky,
    sky2 = Sky2,
    skyContainer = SkyContainer,
    sage = Sage,
    sage2 = Sage2,
    sageContainer = SageContainer,
    forest = Forest,
    sandDeep = SandDeep,
    line = Line,
    inkSoft = InkSoft,
    inkMuted = InkMuted,
    onPhoto = White,
    glassFill = White.copy(alpha = 0.34f),
    glassBorder = White.copy(alpha = 0.55f),
    glassField = White.copy(alpha = 0.55f),
    shadow = Ink.copy(alpha = 0.16f),
    levels = mapOf(
        TatarLevel.A1 to LevelColors(Color(0xFFDCE8D2), Color(0xFF2F5A3F), Color(0xFF5A8B54)),
        TatarLevel.A2 to LevelColors(Color(0xFFC4DDB4), Color(0xFF244A32), Color(0xFF4F8350)),
        TatarLevel.B1 to LevelColors(Color(0xFFF3E2B4), Color(0xFF6B4A0F), Color(0xFFB8811F)),
        TatarLevel.B2 to LevelColors(Color(0xFFF5D3C0), Color(0xFF7A3B2E), Color(0xFFB4522E)),
        TatarLevel.C1 to LevelColors(Color(0xFFEBD0D8), Color(0xFF5E2A3C), Color(0xFF8C3B4A))
    )
)

val DarkTatlibColors = TatlibColors(
    terracotta = TerracottaLight,
    terracottaDeep = TerracottaLightDeep,
    sunset = TerracottaLight,
    peach = NightPeach,
    peach2 = NightPeach2,
    peachContainer = NightPeach,
    sky = NightSky,
    sky2 = NightSky2,
    skyContainer = NightSkyContainer,
    sage = SageLight,
    sage2 = SageLight,
    sageContainer = NightSageContainer,
    forest = SageLight,
    sandDeep = NightSandDeep,
    line = NightLine,
    inkSoft = NightInkSoft,
    inkMuted = NightInkMuted,
    onPhoto = White,
    glassFill = White.copy(alpha = 0.34f),
    glassBorder = White.copy(alpha = 0.55f),
    glassField = White.copy(alpha = 0.55f),
    shadow = Color.Transparent,
    levels = mapOf(
        TatarLevel.A1 to LevelColors(Color(0xFF2F4A36), Color(0xFFC9DDBB), Color(0xFFA9C79A)),
        TatarLevel.A2 to LevelColors(Color(0xFF3A5C40), Color(0xFFD6E8C6), Color(0xFF7FB07A)),
        TatarLevel.B1 to LevelColors(Color(0xFF5A4620), Color(0xFFF3E2B4), Color(0xFFE2B457)),
        TatarLevel.B2 to LevelColors(Color(0xFF6B3A2A), Color(0xFFF5D3C0), Color(0xFFE08A66)),
        TatarLevel.C1 to LevelColors(Color(0xFF5A2E3C), Color(0xFFEBD0D8), Color(0xFFD08A9A))
    )
)

val LocalTatlibColors = staticCompositionLocalOf { LightTatlibColors }

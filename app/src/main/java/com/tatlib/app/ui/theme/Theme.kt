package com.tatlib.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.sp

// MASTER.md § 2.1 — роли M3 для светлой темы.
private val LightColors = lightColorScheme(
    primary = Ink,
    onPrimary = Cream,
    primaryContainer = SageContainer,
    onPrimaryContainer = Ink,
    secondary = Forest,
    onSecondary = Cream,
    secondaryContainer = Sage,
    onSecondaryContainer = Ink,
    tertiary = Terracotta,
    onTertiary = White,
    tertiaryContainer = Peach,
    onTertiaryContainer = Ink,
    background = Cream,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Sand,
    onSurfaceVariant = InkSoft,
    surfaceContainerHighest = SandDeep,
    surfaceContainerHigh = Sand,
    surfaceContainer = Paper,
    surfaceContainerLow = Paper,
    surfaceContainerLowest = Paper,
    outline = InkMuted,
    outlineVariant = Line,
    error = ErrorLight,
    onError = White,
    scrim = Ink
)

// MASTER.md § 2.2 — отдельная тёмная схема (глубокая зелень, не серый и не сдвиг светлой).
private val DarkColors = darkColorScheme(
    primary = SageLight,
    onPrimary = Night,
    primaryContainer = NightSageContainer,
    onPrimaryContainer = SageContainer,
    secondary = SageLight,
    onSecondary = Night,
    secondaryContainer = NightSageContainer,
    onSecondaryContainer = CreamText,
    tertiary = TerracottaLight,
    onTertiary = Night,
    tertiaryContainer = NightPeach,
    onTertiaryContainer = NightPeachText,
    background = Night,
    onBackground = CreamText,
    surface = NightPaper,
    onSurface = CreamText,
    surfaceVariant = NightSand,
    onSurfaceVariant = NightInkSoft,
    surfaceContainerHighest = NightSandDeep,
    surfaceContainerHigh = NightSand,
    surfaceContainer = NightPaper,
    surfaceContainerLow = NightPaper,
    surfaceContainerLowest = Night,
    outline = NightInkMuted,
    outlineVariant = NightLine,
    error = ErrorDark,
    onError = Night,
    scrim = Night
)

/** Расширенные токены проекта: `MaterialTheme.tatlibColors.peach2` и т. д. */
val MaterialTheme.tatlibColors: TatlibColors
    @Composable @ReadOnlyComposable get() = LocalTatlibColors.current

/** Типографика ридера с учётом настроек пользователя. */
val MaterialTheme.readerTypography: ReaderTypography
    @Composable @ReadOnlyComposable get() = LocalReaderTypography.current

@Composable
fun TatlibTheme(
    darkTheme: Boolean = when (AppPreferences.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    },
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = if (darkTheme) DarkColors else LightColors
    val extended = if (darkTheme) DarkTatlibColors else LightTatlibColors
    val reader = ReaderTypography(
        fontSize = AppPreferences.readerFontSizeSp
            .coerceIn(ReaderTypography.MIN_SP, ReaderTypography.MAX_SP).sp,
        font = AppPreferences.readerFont
    )
    CompositionLocalProvider(
        LocalTatlibColors provides extended,
        LocalReaderTypography provides reader
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

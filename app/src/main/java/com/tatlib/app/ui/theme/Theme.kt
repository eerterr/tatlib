package com.tatlib.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = InkGreen,
    onPrimary = Ivory,
    primaryContainer = MossGreen,
    onPrimaryContainer = InkGreen,
    secondary = Sunset,
    onSecondary = Ivory,
    secondaryContainer = SunsetSoft,
    onSecondaryContainer = SunsetDeep,
    background = Cream,
    onBackground = TextPrimary,
    surface = Ivory,
    onSurface = TextPrimary,
    surfaceVariant = Sand,
    onSurfaceVariant = TextSecondary,
    outline = Divider,
    error = ErrorRed,
    onError = Ivory
)

// The prototype is designed cream-first; dark mode reuses the same warm palette
// pushed a shade darker so screenshots stay legible without a full second pass.
private val DarkColors = darkColorScheme(
    primary = SageGreen,
    onPrimary = InkGreen,
    primaryContainer = ForestGreen,
    onPrimaryContainer = MossGreen,
    secondary = Sunset,
    onSecondary = InkGreen,
    secondaryContainer = SunsetDeep,
    onSecondaryContainer = SunsetSoft,
    background = Color(0xFF14201C),
    onBackground = Cream,
    surface = Color(0xFF1C2B26),
    onSurface = Cream,
    surfaceVariant = Color(0xFF25352F),
    onSurfaceVariant = Color(0xFFC4CFC7),
    outline = Color(0xFF3B4A43),
    error = ErrorRed,
    onError = Ivory
)

@Composable
fun TatlibTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

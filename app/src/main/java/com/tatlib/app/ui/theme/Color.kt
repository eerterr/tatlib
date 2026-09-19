package com.tatlib.app.ui.theme

import androidx.compose.ui.graphics.Color

// Palette derived from the Figma moodboards (Kazan sunset over the Kremlin,
// river greenery, folk ornament) rather than the gray wireframe placeholders.

val InkGreen = Color(0xFF1B2E29)      // primary text / primary buttons
val ForestGreen = Color(0xFF3F6552)   // mid accent, active states
val SageGreen = Color(0xFF8FAE86)     // soft accent, success / progress
val MossGreen = Color(0xFFD7E4CE)     // light green chip background

val Cream = Color(0xFFFAF5EC)         // app background
val Sand = Color(0xFFF1E9D8)          // card surfaces on top of cream
val SandDeep = Color(0xFFE6DAC0)      // borders / dividers on sand
val Ivory = Color(0xFFFFFFFF)         // elevated surfaces (search bars, dialogs)

val Sunset = Color(0xFFD98B53)        // terracotta accent, primary CTA highlight
val SunsetSoft = Color(0xFFF3D9C2)    // soft accent background (badges, tags)
val SunsetDeep = Color(0xFFB5622E)

val TextPrimary = InkGreen
val TextSecondary = Color(0xFF6B7770)
val TextMuted = Color(0xFF9AA79E)
val Divider = Color(0xFFE4DCC9)

val ErrorRed = Color(0xFFB3402A)

// Level badge colors (A1 easiest -> C1 hardest), warm-to-deep green ramp.
val LevelA1 = Color(0xFFBFD6B3)
val LevelA2 = Color(0xFF9CC28E)
val LevelB1 = Color(0xFF6FA25E)
val LevelB2 = Color(0xFF4C8143)
val LevelC1 = Color(0xFF2F5C2E)

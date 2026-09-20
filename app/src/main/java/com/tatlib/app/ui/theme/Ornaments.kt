package com.tatlib.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Татарские узоры из Figma — path data 1:1 из `ORN` в `docs/design/mockups/gen2.py`,
 * viewport 24×24, один цвет (красится `tint` в `Icon`). Роли — MASTER.md § 6:
 * 1 завиток (круглые кнопки, ползунок слоёв) · 2 тюльпан-сердце (фланги фразы, пустое состояние,
 * Recap) · 3 тюльпан с ромбом (разделители, водяной знак 4-й обложки) · 4 лилия (логотип, кольцо
 * уровня) · звезда-ромб (декор экранов теста и прогресса).
 */
object Ornaments {

    private fun vector(name: String, vararg paths: String, fillType: PathFillType = PathFillType.NonZero): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            paths.forEach { d ->
                addPath(pathData = addPathNodes(d), pathFillType = fillType, fill = SolidColor(Color.Black))
            }
        }.build()

    /** 1 — двойная стрелка-завиток. */
    val Curl: ImageVector by lazy {
        vector(
            "orn_curl",
            "M2.5 3.5c5.2 1.2 8.6 4.2 9.8 8.5-1.2 4.3-4.6 7.3-9.8 8.5 3.4-2.3 5.4-5.1 5.4-8.5S5.9 5.8 2.5 3.5z",
            "M11 2.5c5.8 1.4 9.6 4.8 10.8 9.5-1.2 4.7-5 8.1-10.8 9.5 3.8-2.6 6-5.7 6-9.5s-2.2-6.9-6-9.5z"
        )
    }

    /** 2 — тюльпан-сердце с завитками. Внутреннее сердце в SVG залито цветом бумаги;
     *  здесь оно вырезано EvenOdd, чтобы узор оставался одноцветным и красился tint. */
    val Tulip: ImageVector by lazy {
        vector(
            "orn_tulip",
            "M12 1.5C9.8 5 6.2 6.2 6.2 10.4c0 2.1 1 3.5 2.6 4.6L12 12.2l3.2 2.8c1.6-1.1 2.6-2.5 2.6-4.6 0-4.2-3.6-5.4-5.8-8.9z" +
                "M12 8.6c-1.4-1.6-4-.7-4 1.3 0 1.6 2.3 3 4 4.6 1.7-1.6 4-3 4-4.6 0-2-2.6-2.9-4-1.3z",
            "M3.6 13.2c-2.3.3-3.9 2.6-2.8 4.9.6-1.6 2.2-2.7 3.9-2.3 2 .4 2.6 2.7 1 3.6 2.6-.4 3.9-2.9 3-5.1-.9-1.6-3.1-1.6-5.1-1.1z",
            "M20.4 13.2c2.3.3 3.9 2.6 2.8 4.9-.6-1.6-2.2-2.7-3.9-2.3-2 .4-2.6 2.7-1 3.6-2.6-.4-3.9-2.9-3-5.1.9-1.6 3.1-1.6 5.1-1.1z",
            fillType = PathFillType.EvenOdd
        )
    }

    /** 3 — тюльпан с ромбом. */
    val Diamond: ImageVector by lazy {
        vector(
            "orn_diamond",
            "M12 1l3.2 5.2L12 11.4 8.8 6.2z",
            "M11.3 12c-1.1 4.2-4.3 7.4-9.3 8.6.2-5.2 3.4-8.4 8.6-9.4z",
            "M12.7 12c1.1 4.2 4.3 7.4 9.3 8.6-.2-5.2-3.4-8.4-8.6-9.4z",
            "M11.3 11.5h1.4v11.5h-1.4z"
        )
    }

    /** 4 — тюльпан-лилия (росток-логотип, кольцо уровня). */
    val Lily: ImageVector by lazy {
        vector(
            "orn_lily",
            "M12 1c-2.6 3.2-3 7.4 0 11.6 3-4.2 2.6-8.4 0-11.6z",
            "M7.6 8.6c-2.2.3-3.8 2.2-3 4.3.6-1.3 2.1-1.9 3.3-1.3 1.1.5 1.3 2 .3 2.8 2-.2 3.3-2 2.8-3.7-.4-1.5-2-2.4-3.4-2.1z",
            "M16.4 8.6c2.2.3 3.8 2.2 3 4.3-.6-1.3-2.1-1.9-3.3-1.3-1.1.5-1.3 2-.3 2.8-2-.2-3.3-2-2.8-3.7.4-1.5 2-2.4 3.4-2.1z",
            "M12 12.4c-1.3 2.8-1.1 5.6 0 8.6 1.1-3 1.3-5.8 0-8.6z",
            "M10.8 16.2c-3.2-1.2-7 .1-9.2 2.9 3.3.6 7.1-.3 9.2-2.9z",
            "M13.2 16.2c3.2-1.2 7 .1 9.2 2.9-3.3.6-7.1-.3-9.2-2.9z"
        )
    }

    /** Четырёхконечная звезда-ромб (декор `star()` в gen2.py). */
    val Star: ImageVector by lazy {
        vector(
            "orn_star",
            "M12 .5c.9 6.4 5.1 10.6 11.5 11.5C17.1 12.9 12.9 17.1 12 23.5 11.1 17.1 6.9 12.9.5 12 6.9 11.1 11.1 6.9 12 .5z"
        )
    }
}

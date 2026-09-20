package com.tatlib.app.ui.theme

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// MASTER.md § 8: extraSmall 12 (обложки) · small 14 (поля) · medium 20 (карточки) ·
// large 24 (крупные карточки) · extraLarge 28 (листы).
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

val PillShape = RoundedCornerShape(50)
val SheetTopShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
val CoverShape = RoundedCornerShape(16.dp)   // обложки в лентах 150×225 r16 (v2)
val TileShape = RoundedCornerShape(18.dp)    // плитки категорий, мини-бар
val MiniBarShape = RoundedCornerShape(18.dp)

/**
 * Арка-купол для фото на экранах теста — путь `arch()` из `docs/design/mockups/gen2.py`,
 * координаты относительно ширины w и высоты h контейнера (перенос 1:1).
 */
val ArchShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w / 2, 0f)
    cubicTo(w / 2, 0f, w * .78f, h * .12f, w * .95f, h * .27f)
    cubicTo(w * 1.03f, h * .34f, w * 1.02f, h * .43f, w, h * .46f)
    lineTo(w, h * .9f)
    quadraticBezierTo(w, h, w * .85f, h)
    lineTo(w * .15f, h)
    quadraticBezierTo(0f, h, 0f, h * .9f)
    lineTo(0f, h * .46f)
    cubicTo(-w * .02f, h * .43f, -w * .03f, h * .34f, w * .05f, h * .27f)
    cubicTo(w * .22f, h * .12f, w / 2, 0f, w / 2, 0f)
    close()
}

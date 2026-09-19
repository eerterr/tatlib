package com.tatlib.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(30.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

// A few standalone shapes used in places Material's Shapes slots don't cover.
val PillShape = RoundedCornerShape(50)
val CardShape = RoundedCornerShape(20.dp)
val SheetTopShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

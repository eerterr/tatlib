package com.tatlib.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

/**
 * Full-bleed "Kazan sunset" hero used behind the onboarding / level-intro screens —
 * a lightweight stand-in for the photographic moodboard backgrounds in Figma.
 */
@Composable
fun KazanSunsetHero(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val horizon = h * 0.56f

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF2C3B58),
                    Color(0xFFB5622E),
                    Color(0xFFF3D9C2)
                ),
                endY = horizon
            )
        )
        drawCircle(
            color = Color(0xFFFCEFDD),
            radius = w * 0.16f,
            center = Offset(w * 0.5f, horizon * 0.7f)
        )
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1E3B3C), Color(0xFF0D2220)),
                startY = horizon,
                endY = h
            ),
            topLeft = Offset(0f, horizon),
            size = Size(w, h - horizon)
        )
        // river reflection line
        drawLine(
            color = Color(0xFFF3D9C2).copy(alpha = 0.25f),
            start = Offset(w * 0.5f, horizon),
            end = Offset(w * 0.5f, h),
            strokeWidth = w * 0.05f
        )

        val skylineColor = Color(0xFF10201D)
        val towers = listOf(0.18f to 0.10f, 0.30f to 0.16f, 0.5f to 0.07f, 0.68f to 0.14f, 0.82f to 0.09f)
        towers.forEach { (fx, fh) ->
            val towerW = w * 0.05f
            val towerH = h * fh
            drawRect(
                color = skylineColor,
                topLeft = Offset(w * fx - towerW / 2, horizon - towerH),
                size = Size(towerW, towerH)
            )
            val domePath = Path().apply {
                moveTo(w * fx - towerW / 2, horizon - towerH)
                cubicTo(
                    w * fx - towerW / 2, horizon - towerH - towerW * 0.9f,
                    w * fx + towerW / 2, horizon - towerH - towerW * 0.9f,
                    w * fx + towerW / 2, horizon - towerH
                )
                close()
            }
            drawPath(domePath, color = skylineColor)
        }
    }
}

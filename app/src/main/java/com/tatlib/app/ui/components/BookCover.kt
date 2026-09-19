package com.tatlib.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.tatlib.app.ui.theme.AppShapes

/**
 * Small generative "cover art" so the prototype doesn't depend on downloading the
 * painted illustrations from Figma. Each book id gets a distinct, hand-tuned scene
 * in the same warm palette as the rest of the app — swap this composable for a real
 * `Image(painter = ...)` once the final artwork is exported as drawables.
 */
@Composable
fun BookCoverArt(bookId: String, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .clip(AppShapes.medium)
    ) {
        when (bookId) {
            "su-anasy" -> drawSuAnasyCover(this)
            "shurale" -> drawShuraleCover(this)
            "najip" -> drawNajipCover(this)
            else -> drawGenericCover(this)
        }
    }
}

private fun drawSuAnasyCover(scope: DrawScope) = with(scope) {
    val w = size.width
    val h = size.height
    val horizon = h * 0.6f

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF2C4A55), Color(0xFF3F6552)),
            endY = horizon
        ),
        size = Size(w, horizon)
    )
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF1B3B3A), Color(0xFF0F2624)),
            startY = horizon,
            endY = h
        ),
        topLeft = Offset(0f, horizon),
        size = Size(w, h - horizon)
    )
    // moon
    drawCircle(color = Color(0xFFF3E9CF), radius = w * 0.11f, center = Offset(w * 0.72f, h * 0.22f))
    // ripples
    val rippleColor = Color(0xFFEFE6CE).copy(alpha = 0.35f)
    for (i in 0..2) {
        val ry = horizon + (h - horizon) * (0.35f + i * 0.2f)
        drawLine(
            color = rippleColor,
            start = Offset(w * 0.15f, ry),
            end = Offset(w * 0.85f, ry - 4f),
            strokeWidth = 2f
        )
    }
    // simple lily silhouette, bottom-left
    drawCircle(color = Color(0xFFEFE6CE).copy(alpha = 0.55f), radius = w * 0.09f, center = Offset(w * 0.22f, h * 0.86f))
}

private fun drawShuraleCover(scope: DrawScope) = with(scope) {
    val w = size.width
    val h = size.height
    val horizon = h * 0.68f

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFE9E2C9), Color(0xFFBFD6B3)),
            endY = horizon
        ),
        size = Size(w, horizon)
    )
    drawCircle(color = Color(0xFFF3D9C2), radius = w * 0.16f, center = Offset(w * 0.28f, h * 0.34f))
    drawRect(
        color = Color(0xFF244B33),
        topLeft = Offset(0f, horizon),
        size = Size(w, h - horizon)
    )
    // layered pine silhouettes
    val treeColors = listOf(Color(0xFF1B3A26), Color(0xFF16311F), Color(0xFF0F2417))
    treeColors.forEachIndexed { index, color ->
        val baseY = horizon + index * (h - horizon) * 0.12f
        val path = Path().apply {
            moveTo(0f, baseY + 26f)
            var x = 0f
            var up = true
            while (x < w) {
                val peakY = if (up) baseY - 22f else baseY + 6f
                lineTo(x + w * 0.08f, peakY)
                x += w * 0.08f
                up = !up
            }
            lineTo(w, baseY + 26f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(path, color = color)
    }
}

private fun drawNajipCover(scope: DrawScope) = with(scope) {
    val w = size.width
    val h = size.height
    val horizon = h * 0.62f

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF3B4A6B), Color(0xFFD98B53), Color(0xFFF3D9C2)),
            endY = horizon
        ),
        size = Size(w, horizon)
    )
    drawCircle(color = Color(0xFFFCEFDD), radius = w * 0.14f, center = Offset(w * 0.5f, horizon * 0.78f))
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF2A4A4A), Color(0xFF13292A)),
            startY = horizon,
            endY = h
        ),
        topLeft = Offset(0f, horizon),
        size = Size(w, h - horizon)
    )
    // simple skyline: a couple of minaret-like silhouettes on the horizon
    val skylineColor = Color(0xFF1B2E29).copy(alpha = 0.75f)
    val towerXs = listOf(0.30f, 0.42f, 0.62f, 0.78f)
    towerXs.forEachIndexed { i, fx ->
        val towerW = w * 0.035f
        val towerH = h * (0.05f + (i % 2) * 0.035f)
        drawRect(
            color = skylineColor,
            topLeft = Offset(w * fx - towerW / 2, horizon - towerH),
            size = Size(towerW, towerH)
        )
        drawCircle(
            color = skylineColor,
            radius = towerW * 0.7f,
            center = Offset(w * fx, horizon - towerH)
        )
    }
}

private fun drawGenericCover(scope: DrawScope) = with(scope) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFBFD6B3), Color(0xFF3F6552))
        )
    )
}

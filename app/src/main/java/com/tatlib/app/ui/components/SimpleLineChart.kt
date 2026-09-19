package com.tatlib.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/** Tiny progress trend line — enough to sell "your progress over time" without a
 *  charting library dependency. Values are expected in the 0f..1f range. */
@Composable
fun SimpleLineChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    dotColor: Color = MaterialTheme.colorScheme.secondary
) {
    Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas
        val w = size.width
        val h = size.height
        val stepX = w / (values.size - 1)

        val points = values.mapIndexed { index, value ->
            Offset(index * stepX, h - (value.coerceIn(0f, 1f) * h))
        }

        for (i in 0 until points.lastIndex) {
            drawLine(
                color = lineColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 5f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
        drawCircle(color = dotColor, radius = 9f, center = points.last())
        drawCircle(
            color = lineColor.copy(alpha = 0.15f),
            radius = 16f,
            center = points.last(),
            style = Stroke(width = 4f)
        )
    }
}

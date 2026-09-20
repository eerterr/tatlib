package com.tatlib.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance

/**
 * Тёмная ли текущая схема. Нужно там, где tatlib.css задаёт для `.dark` другую роль,
 * а не другое значение той же роли (мини-бар, трек прогресса, текст пилюли «Укырга»).
 */
internal val ColorScheme.isDark: Boolean get() = background.luminance() < 0.5f

/** Pressed по MASTER § 7: масштаб 0.98 за 120 мс. */
internal fun Modifier.pressScale(interactionSource: InteractionSource): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, tween(120), label = "press")
    graphicsLayer { scaleX = scale; scaleY = scale }
}

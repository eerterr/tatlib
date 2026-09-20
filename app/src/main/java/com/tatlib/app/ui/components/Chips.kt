package com.tatlib.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tatlib.app.data.TatarLevel
import com.tatlib.app.ui.theme.PillShape
import com.tatlib.app.ui.theme.tatlibColors

/**
 * Чип уровня `.lv`: 28 dp, контейнер/текст из шкалы § 2.3.
 * Где используется: BookDetail, всплывашка слова, профиль, лист «Китап табылды», строки книг.
 */
@Composable
fun LevelChip(level: TatarLevel, modifier: Modifier = Modifier) {
    LevelPill(level, modifier, height = 28.dp, horizontalPadding = 10.dp, style = MaterialTheme.typography.labelMedium)
}

/** Точка уровня `.dot` — маленький индикатор ≥ 12 dp. Где используется: лист компонентов, метки в ридере. */
@Composable
fun LevelDot(level: TatarLevel, modifier: Modifier = Modifier, size: Dp = 12.dp) {
    Box(modifier.size(size).background(MaterialTheme.tatlibColors.level(level).dot, CircleShape))
}

/**
 * Ряд A1–C1 `level_row()`: выбранный — круг 60 dp ink/cream, остальные — чипы 42 dp.
 * Где используется: результат теста уровня.
 */
@Composable
fun LevelRow(selected: TatarLevel, onSelect: (TatarLevel) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TatarLevel.entries.forEach { level ->
            val interaction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .heightIn(min = 60.dp)
                    .clickable(interactionSource = interaction, indication = null) { onSelect(level) },
                contentAlignment = Alignment.Center
            ) {
                if (level == selected) {
                    Box(
                        modifier = Modifier.size(60.dp).background(MaterialTheme.colorScheme.onBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Макет: 600/19 — ближайший слот темы titleLarge (600/20).
                        Text(level.label, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.background)
                    }
                } else {
                    LevelPill(level, Modifier, height = 42.dp, horizontalPadding = 14.dp, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun LevelPill(level: TatarLevel, modifier: Modifier, height: Dp, horizontalPadding: Dp, style: TextStyle) {
    val colors = MaterialTheme.tatlibColors.level(level)
    Box(
        modifier = modifier
            .height(height)
            .background(colors.container, PillShape)
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(level.label, style = style, color = colors.text)
    }
}

/**
 * Чип фильтра `.chip`: 36 dp, sand/inkSoft, выбранный ink/cream. Тач-цель 48 dp снаружи.
 * Где используется: главная (Барысы · Әкиятләр …), прогресс (Атна · Ай · Ел), профиль, BookDetail (жанр).
 */
@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    ChipFrame(
        modifier = modifier,
        onClick = onClick,
        background = if (selected) scheme.onBackground else scheme.surfaceVariant,
        content = if (selected) scheme.background else scheme.onSurfaceVariant,
        borderColor = null,
        text = text
    )
}

/**
 * Чип на фото `.chip.glassy`: белый 40 % с рамкой 60 %, выбранный — белый с тёмным текстом.
 * Где используется: возрастные группы на регистрации.
 */
@Composable
fun GlassChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val white = MaterialTheme.tatlibColors.onPhoto
    ChipFrame(
        modifier = modifier,
        onClick = onClick,
        background = if (selected) white else white.copy(alpha = 0.4f),
        content = if (selected) MaterialTheme.colorScheme.scrim else white,
        borderColor = if (selected) null else white.copy(alpha = 0.6f),
        text = text
    )
}

@Composable
private fun ChipFrame(
    modifier: Modifier,
    onClick: () -> Unit,
    background: Color,
    content: Color,
    borderColor: Color?,
    text: String
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(PillShape)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(36.dp)
                .background(background, PillShape)
                .then(if (borderColor != null) Modifier.border(1.dp, borderColor, PillShape) else Modifier)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text, style = MaterialTheme.typography.titleSmall, color = content)
        }
    }
}

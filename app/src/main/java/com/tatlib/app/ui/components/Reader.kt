package com.tatlib.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tatlib.app.R
import com.tatlib.app.data.TatarLevel
import com.tatlib.app.ui.theme.Ornaments
import com.tatlib.app.ui.theme.PillShape
import com.tatlib.app.ui.theme.ReaderTypography
import com.tatlib.app.ui.theme.tatlibColors
import kotlin.math.roundToInt

private val LAYER_VALUES = listOf(0f, 0.5f, 1f)

/**
 * Переключатель слоёв `.seg`: Русча (0) · Адаптация (0.5) · Оригинал (1), ползунок ink с узором 1,
 * переезд 220 мс FastOutSlowIn. `adaptedEnabled = false` — «Адаптация» недоступна (сканер без API).
 * Где используется: ридер, сканер (текст). Значения как в AppViewModel.selectVersion.
 */
@Composable
fun LayerSwitch(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    adaptedEnabled: Boolean = true
) {
    val scheme = MaterialTheme.colorScheme
    val labels = listOf(
        stringResource(R.string.layer_russian),
        stringResource(R.string.layer_adapted),
        stringResource(R.string.layer_original)
    )
    val selectedIndex = LAYER_VALUES.indexOfFirst { it == value }.coerceAtLeast(0)
    // tatlib.css: светлая — ink/bg, тёмная — forest/night (= primary/onPrimary в обеих схемах).
    val thumbColor = scheme.primary
    val activeText = scheme.onPrimary
    val inactiveText = scheme.onSurfaceVariant
    val groupLabel = stringResource(R.string.layer_switch_label)
    val position by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "layer"
    )
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(scheme.surfaceVariant, PillShape)
            .padding(3.dp)
            .semantics { contentDescription = groupLabel }
            .selectableGroup()
    ) {
        val gap = 2.dp
        val segment = (maxWidth - gap * 2) / 3
        Box(
            modifier = Modifier
                .offset(x = (segment + gap) * position)
                .width(segment)
                .fillMaxHeight()
                .background(thumbColor, PillShape)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
            labels.forEachIndexed { index, label ->
                val on = index == selectedIndex
                val enabled = index != 1 || adaptedEnabled
                val interaction = remember { MutableInteractionSource() }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(PillShape)
                        .alpha(if (enabled) 1f else 0.38f)
                        .selectable(
                            selected = on,
                            enabled = enabled,
                            interactionSource = interaction,
                            indication = null
                        ) { onValueChange(LAYER_VALUES[index]) },
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (on) {
                        Icon(Ornaments.Curl, contentDescription = null, tint = activeText, modifier = Modifier.size(12.dp))
                    }
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (on) activeText else inactiveText,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Всплывашка слова (§ 7.14): слово, чип уровня, перевод, пример, «Ябарга».
 * Где используется: ридер под строкой слова (скролл сохраняется).
 */
@Composable
fun WordPopup(
    word: String,
    level: TatarLevel?,
    translation: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    example: String? = null
) {
    val colors = MaterialTheme.tatlibColors
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, shape, ambientColor = colors.shadow, spotColor = colors.shadow)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, colors.line, shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(word, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            if (level != null) LevelChip(level)
        }
        Text(translation, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        if (example != null) {
            Text(example, style = MaterialTheme.typography.bodySmall, color = colors.inkMuted)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextLink(stringResource(R.string.action_close), onClick = onClose)
        }
    }
}

/**
 * Слайдер кегля ридера (§ 7.23): 16–20 sp шаг 1, «Аа» по краям, трек sand, активная часть forest.
 * Где используется: профиль, лист настроек шрифта в ридере.
 */
@Composable
fun FontSizeSlider(
    valueSp: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val forest = MaterialTheme.tatlibColors.forest
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.reader_font_sample), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
        Slider(
            value = valueSp.toFloat(),
            onValueChange = { onChange(it.roundToInt()) },
            valueRange = ReaderTypography.MIN_SP.toFloat()..ReaderTypography.MAX_SP.toFloat(),
            steps = ReaderTypography.MAX_SP - ReaderTypography.MIN_SP - 1,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = forest,
                activeTrackColor = forest,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )
        Text(stringResource(R.string.reader_font_sample), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

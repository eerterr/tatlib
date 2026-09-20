package com.tatlib.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tatlib.app.data.TatarLevel
import com.tatlib.app.ui.theme.Ornaments
import com.tatlib.app.ui.theme.tatlibColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * Линия прогресса `.bar`: 4 dp, трек sand (тёмная — sandDeep), заливка forest; для сканера — `color = sky`.
 * Где используется: ридер (низ), квиз, сканер (распознавание).
 */
@Composable
fun ProgressLine(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.tatlibColors.forest
) {
    val scheme = MaterialTheme.colorScheme
    val track = if (scheme.isDark) scheme.surfaceContainerHighest else scheme.surfaceVariant
    val shape = RoundedCornerShape(2.dp)
    Box(modifier.fillMaxWidth().height(4.dp).clip(shape).background(track)) {
        Box(Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).fillMaxHeight().background(color, shape))
    }
}

/**
 * Кольцо уровня `ring()`: 120 dp, дуга forest 0..fraction поверх sand, внутри «B1» displayMedium,
 * снаружи 12 лилий (мотив 4) цветом line.
 * Где используется: «Алгарышың».
 */
@Composable
fun LevelRing(
    level: TatarLevel,
    modifier: Modifier = Modifier,
    fraction: Float = 0.5f
) {
    val forest = MaterialTheme.tatlibColors.forest
    val sand = MaterialTheme.colorScheme.surfaceVariant
    val line = MaterialTheme.tatlibColors.line
    val inner = MaterialTheme.colorScheme.background
    Box(modifier.size(120.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(120.dp)) {
            drawCircle(sand)
            drawArc(forest, startAngle = -90f, sweepAngle = 360f * fraction.coerceIn(0f, 1f), useCenter = true)
        }
        // Лепестки: rotate(i·30°) translateY(−62) из макета → координаты по кругу радиуса 62 dp.
        for (i in 0 until 12) {
            val angle = Math.toRadians(i * 30.0)
            Icon(
                Ornaments.Lily,
                contentDescription = null,
                tint = line,
                modifier = Modifier
                    .offset(x = 62.dp * sin(angle).toFloat(), y = -62.dp * cos(angle).toFloat())
                    .size(14.dp)
                    .rotate(i * 30f)
            )
        }
        Box(Modifier.size(100.dp).background(inner, CircleShape), contentAlignment = Alignment.Center) {
            Text(level.label, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

/**
 * Waffle недели `.waffle`: 7 клеток 24×24 r6 зазор 4, активные forest, пустые sand, подписи дней под клетками.
 * Где используется: «Алгарышың» («Бу атна»). `activeDays` — индексы 0..6, `labels` — 7 подписей (Дш … Як).
 */
@Composable
fun WeekWaffle(
    activeDays: Set<Int>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val forest = MaterialTheme.tatlibColors.forest
    val sand = MaterialTheme.colorScheme.surfaceVariant
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 0 until 7) {
                Box(Modifier.size(24.dp).background(if (i in activeDays) forest else sand, RoundedCornerShape(6.dp)))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 0 until 7) {
                Text(
                    labels.getOrElse(i) { "" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.tatlibColors.inkMuted,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.width(24.dp)
                )
            }
        }
    }
}

/**
 * Число со статистикой `stat()`: headlineSmall (Playfair 600, tabular nums) + оверлайн inkMuted.
 * Где используется: BookDetail (929 сүз …), Recap, плитки на «Алгарышың». Пусто — «—».
 */
@Composable
fun StatValue(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontFeatureSettings = "tnum"), color = MaterialTheme.colorScheme.onBackground)
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.tatlibColors.inkMuted)
    }
}

// Градиент .wave из tatlib.css: 135°, #4E6B48 0 % → #6F8F5A 45 % → #3F6F8A 100 %. Единственные литералы цвета в компонентах.
private val WaveStart = Color(0xFF4E6B48)
private val WaveMid = Color(0xFF6F8F5A)
private val WaveEnd = Color(0xFF3F6F8A)

/**
 * Карточка фразы `phrase_card()`: градиент, три звезды, тюльпаны по флангам, фраза Playfair Italic, подпись.
 * Где используется: «Алгарышың», поиск.
 */
@Composable
fun PhraseCard(
    phrase: String,
    source: String,
    modifier: Modifier = Modifier
) {
    val white = MaterialTheme.tatlibColors.onPhoto
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    0f to WaveStart, 0.45f to WaveMid, 1f to WaveEnd,
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
    ) {
        // Звёзды в позициях макета (342 dp карточка): 44 @ (250, −8), 18 @ (300, 70), 12 @ (22, 88).
        DecorStar(44.dp, white.copy(alpha = 0.35f), Modifier.align(Alignment.TopEnd).offset(x = -48.dp, y = -8.dp))
        DecorStar(18.dp, white.copy(alpha = 0.6f), Modifier.align(Alignment.TopEnd).offset(x = -24.dp, y = 70.dp))
        DecorStar(12.dp, white.copy(alpha = 0.5f), Modifier.align(Alignment.TopStart).offset(x = 22.dp, y = 88.dp))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Ornaments.Tulip, contentDescription = null, tint = white, modifier = Modifier.size(22.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(phrase, style = MaterialTheme.typography.displaySmall, color = white, textAlign = TextAlign.Center)
                Text(source.uppercase(), style = MaterialTheme.typography.labelSmall, color = white.copy(alpha = 0.85f), textAlign = TextAlign.Center)
            }
            Icon(Ornaments.Tulip, contentDescription = null, tint = white, modifier = Modifier.size(22.dp).scale(scaleX = -1f, scaleY = 1f))
        }
    }
}

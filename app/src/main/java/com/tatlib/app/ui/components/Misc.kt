package com.tatlib.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tatlib.app.data.TatarLevel
import com.tatlib.app.ui.theme.LightTatlibColors
import com.tatlib.app.ui.theme.PillShape

// Временная связка со старыми экранами до шага 4.2 (там LevelChip берёт цвета из темы).
fun colorForLevel(level: TatarLevel): Color = LightTatlibColors.level(level).dot

@Composable
fun LevelBadge(level: TatarLevel, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(colorForLevel(level).copy(alpha = 0.22f), PillShape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            level.label,
            style = MaterialTheme.typography.labelMedium,
            color = colorForLevel(level),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TagChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, PillShape)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = modifier
            .background(bg, PillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 7.dp)
    ) {
        Text(text, style = MaterialTheme.typography.bodySmall, color = fg, fontWeight = FontWeight.Medium)
    }
}

/** "Барысын карау →" section title row used above the book carousels on the Library screen. */
@Composable
fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.headlineLarge)
        if (actionLabel != null) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable(onClick = onActionClick),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(actionLabel, style = MaterialTheme.typography.bodyLarge)
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
    }
}

@Composable
fun QuoteCard(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun StatTile(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
            .padding(padding),
        content = content
    )
}

package com.tatlib.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CenterFocusWeak
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tatlib.app.R
import com.tatlib.app.data.Book
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.MiniBarShape
import com.tatlib.app.ui.theme.PillShape
import com.tatlib.app.ui.theme.tatlibColors

private class NavItem(val route: String, val label: Int, val icon: ImageVector)

/**
 * Нижняя навигация `.nav` 80 dp: Китапханә · Эзләү · Скан · Алгарыш.
 * Где используется: library, search, scanner, progress (Routes.bottomBarRoutes).
 */
@Composable
fun BottomNav(
    selectedRoute: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember {
        listOf(
            NavItem(Routes.LIBRARY, R.string.nav_library, Icons.AutoMirrored.Rounded.MenuBook),
            NavItem(Routes.SEARCH, R.string.nav_search, Icons.Rounded.Search),
            NavItem(Routes.SCANNER, R.string.nav_scanner, Icons.Rounded.CenterFocusWeak),
            NavItem(Routes.PROGRESS, R.string.nav_progress, Icons.Rounded.BarChart)
        )
    }
    val line = MaterialTheme.tatlibColors.line
    val lineWidth = with(LocalDensity.current) { 1.dp.toPx() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.surface)
            .drawBehind { drawLine(line, Offset(0f, 0f), Offset(size.width, 0f), lineWidth) }
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val on = item.route == selectedRoute
            val interaction = remember { MutableInteractionSource() }
            val color = if (on) MaterialTheme.colorScheme.onBackground else MaterialTheme.tatlibColors.inkMuted
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .semantics { selected = on }
                    .clickable(interactionSource = interaction, indication = null) { onSelect(item.route) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp, 32.dp)
                        .background(if (on) MaterialTheme.tatlibColors.sageContainer else Color.Transparent, PillShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(item.icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }
                Text(
                    stringResource(item.label).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Мини-бар «Хәзер укыла» `.mini` 64 dp: обложка 36×48, оверлайн, «{title} · {author}», круглая ▶.
 * Где используется: главная и поиск, над нижней навигацией (позиционирует вызывающий).
 */
@Composable
fun MiniReadingBar(
    book: Book,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    // tatlib.css: светлая — ink/bg; тёмная — sandDeep, текст берём onSurface (в CSS там bg = night, нечитаемо).
    val background = if (scheme.isDark) scheme.surfaceContainerHighest else scheme.onBackground
    val content = if (scheme.isDark) scheme.onSurface else scheme.background
    val shadow = scheme.scrim.copy(alpha = 0.28f)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(10.dp, MiniBarShape, ambientColor = shadow, spotColor = shadow)
            .background(background, MiniBarShape)
            .clickable(onClick = onOpen)
            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BookCover(book, width = 36.dp, height = 48.dp, radius = 8.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.mini_now_reading).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = content.copy(alpha = 0.7f)
            )
            Text(
                "${book.title} · ${book.author}",
                style = MaterialTheme.typography.titleMedium,
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        RoundButton(
            onClick = onOpen,
            contentDescription = stringResource(R.string.cd_play),
            color = MaterialTheme.tatlibColors.sage2,
            contentColor = if (scheme.isDark) scheme.onPrimary else scheme.onBackground,
            size = 40.dp,
            icon = Icons.Rounded.PlayArrow,
            iconSize = 20.dp
        )
    }
}

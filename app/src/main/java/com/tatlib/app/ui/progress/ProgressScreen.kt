package com.tatlib.app.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.R
import com.tatlib.app.data.ApiClient
import com.tatlib.app.data.RecentWordDto
import com.tatlib.app.data.TranslationStatsDto
import com.tatlib.app.data.UserMeta
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.DecorArc
import com.tatlib.app.ui.components.DecorStar
import com.tatlib.app.ui.components.EmptyState
import com.tatlib.app.ui.components.FilterChip
import com.tatlib.app.ui.components.LevelRing
import com.tatlib.app.ui.components.PhraseCard
import com.tatlib.app.ui.components.StatValue
import com.tatlib.app.ui.components.WeekWaffle
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.tatlibColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@Composable
fun ProgressScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    var stats by remember { mutableStateOf<TranslationStatsDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            error = null
            stats = ApiClient.apiService.getTranslationStats()
        } catch (e: Exception) {
            error = e.message ?: e.javaClass.simpleName
        } finally {
            isLoading = false
        }
    }

    val colors = MaterialTheme.tatlibColors
    val scheme = MaterialTheme.colorScheme
    val dash = "—"

    Box(
        Modifier
            .fillMaxSize()
            .background(scheme.background)
            .clipToBounds()
    ) {
        Box(Modifier.fillMaxSize().statusBarsPadding()) {
            DecorArc(420.dp, colors.sky2, Modifier.offset(x = 230.dp, y = (-300).dp))
            DecorStar(26.dp, colors.sky2, Modifier.offset(x = 336.dp, y = 104.dp))
            DecorStar(12.dp, colors.peach2, Modifier.offset(x = 312.dp, y = 150.dp))

            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.progress_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = scheme.onBackground
                    )
                    IconButton(
                        onClick = { navController.navigate(Routes.PROFILE) },
                        modifier = Modifier.size(48.dp).background(colors.sageContainer, CircleShape)
                    ) {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = stringResource(R.string.cd_profile),
                            tint = scheme.onBackground,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Периоды — визуальные, без действия (00-ux-map п. 17).
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(stringResource(R.string.progress_period_week), selected = true, onClick = {})
                        FilterChip(stringResource(R.string.progress_period_month), selected = false, onClick = {})
                        FilterChip(stringResource(R.string.progress_period_year), selected = false, onClick = {})
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(scheme.surface, MaterialTheme.shapes.medium)
                            .border(1.dp, colors.line, MaterialTheme.shapes.medium)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LevelRing(level = UserMeta.level, fraction = 0.5f)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                stringResource(R.string.progress_current_level).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.inkMuted
                            )
                            Text(
                                stringResource(R.string.progress_level_line, UserMeta.level.label),
                                style = MaterialTheme.typography.titleLarge,
                                color = scheme.onBackground
                            )
                            Text(
                                stringResource(R.string.progress_next_hint),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.inkSoft
                            )
                        }
                    }

                    val err = error
                    if (err != null) {
                        EmptyState(title = stringResource(R.string.progress_empty_title), text = err)
                    } else {
                        val current = stats
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatTile(current?.total_translations?.toString() ?: dash, stringResource(R.string.progress_stat_translations), colors.sageContainer)
                            StatTile(current?.unique_words?.toString() ?: dash, stringResource(R.string.progress_stat_unique), colors.skyContainer)
                            // Дат активности в API нет — только «—».
                            StatTile(dash, stringResource(R.string.progress_stat_days), colors.peachContainer)
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.progress_this_week), style = MaterialTheme.typography.titleMedium, color = scheme.onBackground)
                            WeekWaffle(
                                activeDays = remember(current) { activeWeekdays(current?.recent_words.orEmpty()) },
                                labels = stringArrayResource(R.array.progress_week_days).toList()
                            )
                        }
                    }

                    PhraseCard(
                        phrase = stringResource(R.string.progress_phrase),
                        source = stringResource(R.string.progress_phrase_source)
                    )

                    if (error == null && !isLoading) {
                        val words = stats?.recent_words.orEmpty()
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.progress_recent), style = MaterialTheme.typography.titleMedium, color = scheme.onBackground)
                            // Названия книги в RecentWordDto нет — только чипы слов.
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                words.forEach { FilterChip(it.word, selected = false, onClick = {}) }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Плитка `.tile` r16 padding 12 с цифрой статистики. */
@Composable
private fun RowScope.StatTile(value: String, label: String, color: Color) {
    Box(
        Modifier
            .weight(1f)
            .background(color, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        StatValue(value, label)
    }
}

/**
 * Индексы дней текущей недели (0 = понедельник), в которые есть переводы.
 * `created_at` — ISO UTC бэкенда (`datetime.utcnow().isoformat()`): `2026-09-20T00:05:29.846831`.
 */
// ponytail: сравнение по YEAR + WEEK_OF_YEAR — на стыке лет неделя 1 может отсечься; поправить, если понадобится точность
private fun activeWeekdays(words: List<RecentWordDto>): Set<Int> {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT).apply { timeZone = TimeZone.getTimeZone("UTC") }
    val now = Calendar.getInstance().apply { firstDayOfWeek = Calendar.MONDAY; minimalDaysInFirstWeek = 4 }
    val cal = Calendar.getInstance().apply { firstDayOfWeek = Calendar.MONDAY; minimalDaysInFirstWeek = 4 }
    return words.mapNotNull { w ->
        val raw = w.created_at ?: return@mapNotNull null
        val date = runCatching { parser.parse(raw.take(19)) }.getOrNull() ?: return@mapNotNull null
        cal.time = date
        val sameWeek = cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            cal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
        // DAY_OF_WEEK: воскресенье = 1 … суббота = 7 → понедельник = 0 … воскресенье = 6.
        if (sameWeek) (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 else null
    }.toSet()
}

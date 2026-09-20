package com.tatlib.app.ui.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.data.ApiClient
import com.tatlib.app.data.PopularWordDto
import com.tatlib.app.data.RecentWordDto
import com.tatlib.app.data.TranslationStatsDto
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.ScreenTopBar

@Composable
fun ProgressScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    var stats by remember {
        mutableStateOf<TranslationStatsDto?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            error = null

            stats = ApiClient.apiService.getTranslationStats()

        } catch (e: Exception) {
            error = e.message ?: "Не удалось загрузить статистику"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.padding(
                24.dp,
                16.dp,
                24.dp,
                8.dp
            )
        ) {
            ScreenTopBar(
                onBack = {
                    navController.popBackStack()
                },
                showLanguageToggle = false
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Text(
                    text = "Алгарыш",
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(
                        top = 8.dp,
                        bottom = 8.dp
                    )
                )
            }

            if (isLoading) {

                item {
                    CircularProgressIndicator()
                }

            } else if (error != null) {

                item {
                    Text(
                        text = error ?: "Хата",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            } else {

                val currentStats = stats

                if (currentStats != null) {

                    item {
                        Text(
                            text = "Переводы",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    item {
                        Text(
                            text = "Всего переводов: ${currentStats.total_translations}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    item {
                        Text(
                            text = "Уникальных слов: ${currentStats.unique_words}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    if (currentStats.popular_words.isNotEmpty()) {

                        item {
                            Text(
                                text = "Популярные слова",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        items(
                            items = currentStats.popular_words
                        ) { word ->
                            PopularWordRow(word)
                        }
                    }

                    if (currentStats.recent_words.isNotEmpty()) {

                        item {
                            Text(
                                text = "Недавние переводы",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        items(
                            items = currentStats.recent_words
                        ) { word ->
                            RecentWordRow(word)
                        }
                    }

                    if (
                        currentStats.popular_words.isEmpty() &&
                        currentStats.recent_words.isEmpty()
                    ) {
                        item {
                            Text(
                                text = "Пока нет переведённых слов.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Тәрҗемә ителгән сүзләр монда саклана.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        vertical = 8.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun PopularWordRow(
    word: PopularWordDto
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = word.word,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Переводов: ${word.count}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RecentWordRow(
    word: RecentWordDto
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = word.word,
            style = MaterialTheme.typography.bodyLarge
        )

        word.translation
            ?.takeIf { it.isNotBlank() }
            ?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
    }
}
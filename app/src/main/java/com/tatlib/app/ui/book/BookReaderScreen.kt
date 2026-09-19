package com.tatlib.app.ui.book

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.data.Book
import com.tatlib.app.data.GlossWord
import com.tatlib.app.data.MockData
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.RoundBackButton
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.AppShapes

private const val GLOSS_TAG = "gloss"

@Composable
fun BookReaderScreen(navController: NavHostController, viewModel: AppViewModel, bookId: String) {
    val book: Book = MockData.bookById(bookId) ?: MockData.shurale
    var pageIndex by remember(bookId) { mutableIntStateOf(initialPageIndex(book, viewModel)) }
    var activeGloss by remember { mutableStateOf<GlossWord?>(null) }

    val page = book.pages[pageIndex]
    val progress = (pageIndex + 1f) / book.pageCount

    fun saveProgress() {
        viewModel.updateProgress(book.id, progress)
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            RoundBackButton(onClick = {
                saveProgress()
                navController.popBackStack()
            })
            Column(modifier = Modifier.weight(1f)) {
                Text("Сложность", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LinearProgressIndicator(
                    progress = { difficultyFraction(book.level) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(AppShapes.extraSmall),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            book.title,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 14.dp, bottom = 20.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            val annotated = remember(page) { buildGlossedText(page.bodyText, page.glossary) }
            ClickableText(
                text = annotated,
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                onClick = { offset ->
                    annotated.getStringAnnotations(GLOSS_TAG, offset, offset).firstOrNull()?.let { ann ->
                        val word = page.glossary.firstOrNull { it.word == ann.item }
                        activeGloss = word
                    }
                }
            )

            if (page.highlightPhrase != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .clip(AppShapes.large)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            page.highlightPhrase,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { /* mock: TTS not wired up in the prototype */ }) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Тыңлау")
                        }
                    }
                }
            }

            AnimatedVisibility(visible = activeGloss != null) {
                activeGloss?.let { gloss ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .clip(AppShapes.medium)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { activeGloss = null }
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                gloss.word,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                gloss.translationRu,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        // Pagination + save row
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextNavButton(
                label = "Алдагы",
                enabled = pageIndex > 0,
                onClick = { pageIndex -= 1; activeGloss = null }
            )
            Text(
                "${pageIndex + 1} / ${book.pageCount}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextNavButton(
                label = if (pageIndex == book.pageCount - 1) "Тәмамлау" else "Киләсе",
                enabled = true,
                onClick = {
                    activeGloss = null
                    if (pageIndex == book.pageCount - 1) {
                        viewModel.updateProgress(book.id, 1f)
                        navController.navigate(Routes.recap(book.id)) {
                            popUpTo(Routes.bookReader(book.id)) { inclusive = true }
                        }
                    } else {
                        val nextIndex = pageIndex + 1
                        pageIndex = nextIndex
                        viewModel.updateProgress(book.id, (nextIndex + 1f) / book.pageCount)
                    }
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .clip(AppShapes.large)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        saveProgress()
                        navController.popBackStack()
                    }
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "Сакларга",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun TextNavButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Text(
        label,
        style = MaterialTheme.typography.titleMedium,
        color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .clip(AppShapes.extraSmall)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    )
}

private fun initialPageIndex(book: Book, viewModel: AppViewModel): Int {
    val progress = viewModel.progressFor(book)
    val index = (progress * book.pageCount).toInt()
    return index.coerceIn(0, book.pageCount - 1)
}

private fun difficultyFraction(level: com.tatlib.app.data.TatarLevel): Float = when (level) {
    com.tatlib.app.data.TatarLevel.A1 -> 0.2f
    com.tatlib.app.data.TatarLevel.A2 -> 0.4f
    com.tatlib.app.data.TatarLevel.B1 -> 0.6f
    com.tatlib.app.data.TatarLevel.B2 -> 0.8f
    com.tatlib.app.data.TatarLevel.C1 -> 1f
}

private fun buildGlossedText(body: String, glossary: List<GlossWord>): AnnotatedString = buildAnnotatedString {
    append(body)
    glossary.forEach { gloss ->
        val start = body.indexOf(gloss.word, ignoreCase = true)
        if (start >= 0) {
            val end = start + gloss.word.length
            addStyle(
                SpanStyle(
                    color = Color(0xFFD98B53),
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                ),
                start,
                end
            )
            addStringAnnotation(tag = GLOSS_TAG, annotation = gloss.word, start = start, end = end)
        }
    }
}

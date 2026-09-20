package com.tatlib.app.ui.book

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.ScreenTopBar

@Composable
fun BookReaderScreen(
    bookId: String,
    navController: NavHostController,
    viewModel: AppViewModel
) {
    val selectedBook by viewModel.selectedBook.collectAsState()
    val selectedVersion by viewModel.selectedVersion.collectAsState()
    val selectedWord by viewModel.selectedWord.collectAsState()
    val selectedWordTranslation by viewModel.selectedWordTranslation.collectAsState()
    val isLoadingTranslation by viewModel.isLoadingTranslation.collectAsState()

    LaunchedEffect(bookId) {
        bookId.toIntOrNull()?.let { id ->
            viewModel.loadBook(id)
        }
    }

    val book = selectedBook

    if (book == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            ScreenTopBar(
                onBack = {
                    navController.popBackStack()
                },
                showLanguageToggle = false
            )

            Text(
                text = "Китап йөкләнә...",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 32.dp)
            )
        }

        return
    }

    var currentPageIndex by remember(book.id) {
        mutableIntStateOf(0)
    }

    val page = book.pages.getOrNull(currentPageIndex)

    if (page == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            ScreenTopBar(
                onBack = {
                    navController.popBackStack()
                },
                showLanguageToggle = false
            )

            Text(
                text = "Текст табылмады",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 32.dp)
            )
        }

        return
    }

    val displayedText = when {
        selectedVersion >= 0.75f -> {
            page.originalText
        }

        selectedVersion >= 0.25f -> {
            page.adaptedText.ifBlank {
                page.originalText
            }
        }

        else -> {
            page.russianText.ifBlank {
                page.originalText
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {

        ScreenTopBar(
            onBack = {
                navController.popBackStack()
            },
            showLanguageToggle = false
        )

        Text(
            text = book.title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(
                top = 12.dp,
                bottom = 4.dp
            )
        )

        Text(
            text = when {
                selectedVersion >= 0.75f -> "Оригинал"
                selectedVersion >= 0.25f -> "Адаптация"
                else -> "Русский перевод"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Slider(
            value = selectedVersion,
            onValueChange = {
                viewModel.selectVersion(it)
            },
            steps = 1,
            valueRange = 0f..1f,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Русский",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Адаптация",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Оригинал",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 24.dp)
        ) {

            WordClickableText(
                text = displayedText,
                onWordClick = { word ->
                    if (selectedVersion > 0f) {
                        viewModel.translateWord(
                            bookId = book.id,
                            word = word
                        )
                    }
                }
            )

            selectedWord?.let { word ->

                TranslationCard(
                    word = word,
                    translation = if (isLoadingTranslation) {
                        "Тәрҗемә..."
                    } else {
                        selectedWordTranslation
                            ?: "Тәрҗемә табылмады"
                    },
                    onClose = {
                        viewModel.clearSelectedWord()
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 12.dp,
                    bottom = 24.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = if (currentPageIndex > 0) {
                    "← Артка"
                } else {
                    ""
                },
                modifier = Modifier
                    .clickable {
                        if (currentPageIndex > 0) {
                            currentPageIndex--
                            viewModel.clearSelectedWord()
                        }
                    }
                    .padding(8.dp)
            )

            Text(
                text = "${currentPageIndex + 1} / ${book.pageCount}",
                modifier = Modifier.padding(8.dp)
            )

            Text(
                text = if (currentPageIndex < book.pageCount - 1) {
                    "Алга →"
                } else {
                    ""
                },
                modifier = Modifier
                    .clickable {
                        if (currentPageIndex < book.pageCount - 1) {
                            currentPageIndex++
                            viewModel.clearSelectedWord()
                        }
                    }
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun WordClickableText(
    text: String,
    onWordClick: (String) -> Unit
) {
    val annotatedText = remember(text) {
        buildWordAnnotatedString(text)
    }

    ClickableText(
        text = annotatedText,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Justify,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
        ),
        onClick = { offset ->

            annotatedText
                .getStringAnnotations(
                    tag = "WORD",
                    start = offset,
                    end = offset
                )
                .firstOrNull()
                ?.let { annotation ->
                    onWordClick(annotation.item)
                }
        }
    )
}

private fun buildWordAnnotatedString(
    text: String
): AnnotatedString {

    return buildAnnotatedString {

        /*
         * ВАЖНО:
         * не используем split(" "),
         * поэтому пробелы, несколько пробелов,
         * переносы строк и табуляции сохраняются.
         */
        val regex = Regex("""\S+|\s+""")

        regex.findAll(text).forEach { match ->

            val part = match.value

            if (part.all { it.isWhitespace() }) {

                // Сохраняем пробелы и переносы строк.
                append(part)

            } else {

                val cleanWord = part.trim()

                pushStringAnnotation(
                    tag = "WORD",
                    annotation = cleanWord
                )

                append(part)

                pop()
            }
        }
    }
}

@Composable
private fun TranslationCard(
    word: String,
    translation: String,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 20.dp,
                bottom = 12.dp
            )
    ) {

        Text(
            text = word,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = translation,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "Закрыть",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable {
                    onClose()
                }
        )
    }
}
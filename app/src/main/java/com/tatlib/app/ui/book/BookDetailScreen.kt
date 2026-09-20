package com.tatlib.app.ui.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.R
import com.tatlib.app.data.BookMetaTable
import com.tatlib.app.data.displayLevel
import com.tatlib.app.data.displayYear
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.BookCover
import com.tatlib.app.ui.components.LevelChip
import com.tatlib.app.ui.components.ReadPill
import com.tatlib.app.ui.components.StatValue
import com.tatlib.app.ui.components.TopBar
import com.tatlib.app.ui.components.TopBarLeft
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.search.genreLabel
import com.tatlib.app.ui.theme.PillShape
import com.tatlib.app.ui.theme.tatlibColors

@Composable
fun BookDetailScreen(
    navController: NavHostController,
    viewModel: AppViewModel,
    bookId: String
) {
    val books by viewModel.books.collectAsState()
    val selectedBook by viewModel.selectedBook.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(bookId) {
        bookId.toIntOrNull()?.let { viewModel.loadBook(it) }
    }

    // Карточка — из списка книг (жанр, автор); текст блоков приходит в selectedBook после loadBook.
    val loaded = selectedBook?.takeIf { it.id == bookId }
    val book = books.firstOrNull { it.id == bookId } ?: loaded

    val scheme = MaterialTheme.colorScheme
    val colors = MaterialTheme.tatlibColors

    if (book == null) {
        Box(Modifier.fillMaxSize().background(scheme.background), contentAlignment = Alignment.Center) {
            when {
                isLoading -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CircularProgressIndicator(color = colors.forest)
                    Text(stringResource(R.string.book_loading), style = MaterialTheme.typography.titleMedium, color = colors.inkSoft)
                }
                error != null -> Text(error.orEmpty(), style = MaterialTheme.typography.bodyLarge, color = scheme.error, modifier = Modifier.padding(24.dp))
                else -> Text(stringResource(R.string.book_not_found), style = MaterialTheme.typography.titleLarge, color = scheme.onBackground)
            }
        }
        return
    }

    // Описание — первый абзац adapted_text первого блока; пока не загружено — ничего.
    val description = loaded?.pages?.firstOrNull()?.adaptedText
        ?.substringBefore("\n\n")?.trim()?.takeIf { it.isNotEmpty() }
    val meta = BookMetaTable.of(book.id)

    Box(Modifier.fillMaxSize().background(scheme.background)) {
        // Фон «страницы альбома»: та же обложка, увеличенная; blur недоступен ниже API 31 — только градиент.
        Box(Modifier.fillMaxWidth().height(470.dp).clipToBounds()) {
            Image(
                painter = painterResource(
                    when (book.id) {
                        "1" -> R.drawable.cover_su_anasy
                        "2" -> R.drawable.cover_shurale
                        "3" -> R.drawable.cover_najip
                        else -> R.drawable.hero_sunset
                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().graphicsLayer { scaleX = 1.3f; scaleY = 1.3f }
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        0.0f to scheme.scrim.copy(alpha = 0.25f),
                        0.55f to scheme.background.copy(alpha = 0.2f),
                        1.0f to scheme.background
                    )
                )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
        ) {
            TopBar(
                left = TopBarLeft.Back,
                onLeft = { navController.popBackStack() },
                onPhoto = true,
                right = {
                    // «…» в макете без действия — меню страницы книги не проектировалось.
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.MoreHoriz, contentDescription = null, tint = colors.onPhoto)
                    }
                }
            )
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BookCover(book, width = 190.dp, height = 285.dp, radius = 18.dp)

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(book.title, style = MaterialTheme.typography.headlineMedium, color = scheme.onBackground, textAlign = TextAlign.Center)
                    Text(
                        stringResource(R.string.meta_pair, book.author, book.displayYear),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.inkSoft,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LevelChip(book.displayLevel)
                        if (book.genre.isNotBlank()) GenreChip(genreLabel(book.genre))
                    }
                }

                ReadPill(
                    text = stringResource(R.string.action_read),
                    onClick = { navController.navigate(Routes.bookReader(book.id)) },
                    fullWidth = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatValue(meta?.wordCount?.toString() ?: "—", stringResource(R.string.book_stat_words))
                    StatValue(meta?.uniqueWords?.toString() ?: "—", stringResource(R.string.book_stat_unique_words))
                    StatValue(meta?.blocks?.toString() ?: "—", stringResource(R.string.book_stat_blocks))
                }

                if (description != null) {
                    Text(description, style = MaterialTheme.typography.bodyLarge, color = scheme.onBackground, modifier = Modifier.fillMaxWidth())
                    Text(
                        stringResource(R.string.book_description_caption),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.inkMuted,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/** Чип жанра `.chip` 28 dp рядом с чипом уровня — без клика (FilterChip в компонентах 36 dp с тач-целью 48). */
@Composable
private fun GenreChip(text: String) {
    Box(
        modifier = Modifier
            .height(28.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, PillShape)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

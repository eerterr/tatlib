package com.tatlib.app.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.R
import com.tatlib.app.data.Book
import com.tatlib.app.data.TatarLevel
import com.tatlib.app.data.displayLevel
import com.tatlib.app.data.displayYear
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.BookRow
import com.tatlib.app.ui.components.CategoryTile
import com.tatlib.app.ui.components.EmptyState
import com.tatlib.app.ui.components.PlainField
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.tatlibColors

// Жанры так приходят из /api/books (backend/books.genre, по-русски) — это данные, не строки интерфейса.
internal const val GENRE_TALE = "сказка в стихах"
internal const val GENRE_STORY = "рассказ"

/** Жанр по-татарски для интерфейса (`row()` в gen2.py); неизвестный — как из API. */
@Composable
internal fun genreLabel(genre: String): String = when (genre) {
    GENRE_TALE -> stringResource(R.string.genre_tale)
    GENRE_STORY -> stringResource(R.string.genre_story)
    else -> genre
}

// TODO backend: уровень пользователя (user_level_state), см. data/UserMeta.kt группы B
private val userLevel = TatarLevel.B1

/** Плитки категорий; тап по плитке фильтрует список «Барлык китаплар», повторный тап снимает фильтр. */
private enum class Category {
    TALES, STORIES, TUKAY, LEVEL;

    fun matches(book: Book): Boolean = when (this) {
        TALES -> book.genre == GENRE_TALE
        STORIES -> book.genre == GENRE_STORY
        TUKAY -> book.author.contains("Тукай")
        LEVEL -> book.displayLevel == userLevel
    }
}

@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf<Category?>(null) }

    val books by viewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val results = books.filter { book ->
        (category?.matches(book) ?: true) &&
            (query.isBlank() || book.title.contains(query, ignoreCase = true) || book.author.contains(query, ignoreCase = true))
    }

    val colors = MaterialTheme.tatlibColors
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.nav_search), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)

            PlainField(
                value = query,
                onValueChange = { query = it },
                placeholder = stringResource(R.string.search_placeholder),
                leadingIcon = Icons.Rounded.Search,
                modifier = Modifier.fillMaxWidth()
            )

            // Сетка 2×2 плиток; цвета из tile() в gen2.py — токены tileSteel / tileHoney в теме.
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Tile(Category.TALES, stringResource(R.string.library_filter_tales), books, colors.forest, R.drawable.cover_shurale, category, { category = it }, Modifier.weight(1f))
                    Tile(Category.STORIES, stringResource(R.string.library_filter_stories), books, colors.terracotta, R.drawable.cover_najip, category, { category = it }, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Tile(Category.TUKAY, stringResource(R.string.library_filter_tukay), books, colors.tileSteel, R.drawable.cover_su_anasy, category, { category = it }, Modifier.weight(1f))
                    Tile(Category.LEVEL, stringResource(R.string.search_tile_level, userLevel.label), books, colors.tileHoney, R.drawable.kremlin_bottom, category, { category = it }, Modifier.weight(1f))
                }
            }

            Text(stringResource(R.string.search_all_books), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)

            when {
                isLoading && books.isEmpty() -> Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = colors.forest)
                    Text(stringResource(R.string.library_loading), style = MaterialTheme.typography.bodyMedium, color = colors.inkSoft)
                }
                error != null && books.isEmpty() -> Text(
                    error.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
                results.isEmpty() -> EmptyState(title = stringResource(R.string.search_nothing_found), text = "")
                else -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    results.forEach { book ->
                        BookRow(
                            book = book,
                            onClick = { navController.navigate(Routes.bookDetail(book.id)) },
                            meta = stringResource(R.string.meta_triple, book.displayLevel.label, genreLabel(book.genre), book.displayYear)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Tile(
    category: Category,
    title: String,
    books: List<Book>,
    color: Color,
    image: Int,
    selected: Category?,
    onSelect: (Category?) -> Unit,
    modifier: Modifier = Modifier
) {
    val count = books.count { category.matches(it) }
    CategoryTile(
        title = title,
        subtitle = pluralStringResource(R.plurals.search_books_count, count, count),
        color = color,
        image = painterResource(image),
        onClick = { onSelect(if (selected == category) null else category) },
        modifier = modifier
    )
}

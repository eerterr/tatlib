package com.tatlib.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.R
import com.tatlib.app.data.Book
import com.tatlib.app.data.displayLevel
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.ActionButton
import com.tatlib.app.ui.components.ActionStyle
import com.tatlib.app.ui.components.AppLanguage
import com.tatlib.app.ui.components.BookCard
import com.tatlib.app.ui.components.EmptyState
import com.tatlib.app.ui.components.FilterChip
import com.tatlib.app.ui.components.LanguageToggle
import com.tatlib.app.ui.components.OnPhotoLink
import com.tatlib.app.ui.components.PhotoHero
import com.tatlib.app.ui.components.SectionHeader
import com.tatlib.app.ui.components.TextLink
import com.tatlib.app.ui.components.TopBar
import com.tatlib.app.ui.components.TopBarLeft
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.search.GENRE_STORY
import com.tatlib.app.ui.search.GENRE_TALE
import com.tatlib.app.ui.theme.tatlibColors
import java.util.Calendar

/** Чипы над лентами: Барысы · Әкиятләр · Хикәяләр · Тукай. */
private enum class LibraryFilter(val label: Int) {
    ALL(R.string.action_all),
    TALES(R.string.library_filter_tales),
    STORIES(R.string.library_filter_stories),
    TUKAY(R.string.library_filter_tukay);

    fun matches(book: Book): Boolean = when (this) {
        ALL -> true
        TALES -> book.genre == GENRE_TALE
        STORIES -> book.genre == GENRE_STORY
        TUKAY -> book.author.contains("Тукай")
    }
}

@Composable
fun LibraryScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    val books by viewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Загрузка / ошибка / пусто — как в прежнем экране, на плоском фоне (белый текст hero здесь не нужен).
    if (books.isEmpty()) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            when {
                isLoading -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CircularProgressIndicator(color = MaterialTheme.tatlibColors.forest)
                    Text(stringResource(R.string.library_loading), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.tatlibColors.inkSoft)
                }
                error != null -> EmptyState(
                    title = stringResource(R.string.library_load_failed),
                    text = error.orEmpty(),
                    action = { TextLink(stringResource(R.string.library_retry), onClick = { viewModel.loadBooks() }) }
                )
                else -> EmptyState(title = stringResource(R.string.library_empty), text = "")
            }
        }
        return
    }

    // Границы времени суток: до 12 — иртә, 12–17 — көн, с 18 — кич.
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val hero = when {
        hour < 12 -> R.drawable.hero_morning
        hour < 18 -> R.drawable.hero_day
        else -> R.drawable.hero_evening
    }
    val greeting = when {
        hour < 12 -> R.string.library_greeting_morning
        hour < 18 -> R.string.library_greeting_day
        else -> R.string.library_greeting_evening
    }

    var selectedFilter by rememberSaveable { mutableStateOf(LibraryFilter.ALL) }
    val shown = books.filter { selectedFilter.matches(it) }
    // Текущая книга — с прогрессом, иначе первая (00-ux-map п. 7); так же выбирает мини-бар в TatlibApp.
    val continueBook = books.firstOrNull { it.progress > 0f } ?: books.first()

    val scheme = MaterialTheme.colorScheme
    val colors = MaterialTheme.tatlibColors
    val scroll = rememberScrollState()
    val sidePadding = PaddingValues(horizontal = 24.dp)

    Box(Modifier.fillMaxSize().background(scheme.background)) {
        PhotoHero(
            painter = painterResource(hero),
            height = 400.dp,
            overlay = Brush.verticalGradient(
                0.30f to scheme.scrim.copy(alpha = 0.05f),
                0.75f to scheme.scrim.copy(alpha = 0.6f),
                1.0f to scheme.background
            ),
            contentAlignment = Alignment.BottomCenter,
            // Фото уезжает вместе с контентом, иначе тёмные заголовки секций наедут на фото при прокрутке.
            modifier = Modifier.graphicsLayer { translationY = -scroll.value.toFloat() }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .statusBarsPadding()
        ) {
            TopBar(
                left = TopBarLeft.Logo,
                onPhoto = true,
                right = {
                    // Переключение языка — вне scope (00-ux-map п. 21), как в прежнем экране.
                    LanguageToggle(language = AppLanguage.TAT, onToggle = {}, onPhoto = true)
                    IconButton(onClick = { navController.navigate(Routes.PROFILE) }) {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = stringResource(R.string.cd_profile),
                            tint = colors.onPhoto,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 120.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Column(modifier = Modifier.padding(sidePadding), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(greeting), style = MaterialTheme.typography.displayLarge, color = colors.onPhoto)
                    Text(
                        stringResource(R.string.library_continue_question),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onPhoto.copy(alpha = 0.92f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionButton(
                            text = stringResource(R.string.library_continue),
                            onClick = { navController.navigate(Routes.bookReader(continueBook.id)) },
                            style = ActionStyle.OnPhoto
                        )
                        // «Трендлар» в макете без перехода — экрана трендов нет.
                        OnPhotoLink(text = stringResource(R.string.library_trends), onClick = {})
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(sidePadding)
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LibraryFilter.entries.forEach { item ->
                        FilterChip(text = stringResource(item.label), selected = selectedFilter == item, onClick = { selectedFilter = item })
                    }
                }

                Shelf(
                    title = stringResource(R.string.library_my_books),
                    books = shown,
                    subtitle = { stringResource(R.string.meta_pair, it.author, it.displayLevel.label) },
                    onAll = { navController.navigate(Routes.SEARCH) },
                    onBook = { navController.navigate(Routes.bookDetail(it.id)) },
                    sidePadding = sidePadding
                )
                Shelf(
                    title = stringResource(R.string.library_recommended),
                    books = shown.reversed(),
                    subtitle = { it.author },
                    onAll = { navController.navigate(Routes.SEARCH) },
                    onBook = { navController.navigate(Routes.bookDetail(it.id)) },
                    sidePadding = sidePadding
                )
            }
        }
    }
}

/** Секция ленты: заголовок с «Барысы» + ряд карточек 150×225, лента уходит под край экрана. */
@Composable
private fun Shelf(
    title: String,
    books: List<Book>,
    subtitle: @Composable (Book) -> String,
    onAll: () -> Unit,
    onBook: (Book) -> Unit,
    sidePadding: PaddingValues
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = title,
            actionLabel = stringResource(R.string.action_all),
            onAction = onAll,
            modifier = Modifier.padding(sidePadding)
        )
        LazyRow(contentPadding = sidePadding, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(books, key = { it.id }) { book ->
                BookCard(book = book, onClick = { onBook(book) }, subtitle = subtitle(book))
            }
        }
    }
}

package com.tatlib.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.BookGridCard
import com.tatlib.app.ui.components.LanguageToggle
import com.tatlib.app.ui.components.PrimaryButton
import com.tatlib.app.ui.components.SectionHeader
import com.tatlib.app.ui.components.AppLanguage
import com.tatlib.app.ui.theme.AppShapes

@Composable
fun LibraryScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    val books by viewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    /*
     * Берём первую книгу как книгу для блока
     * "Продолжить чтение".
     *
     * Позже это можно заменить реальным сохранением
     * последней открытой книги.
     */
    val continueBook = books.firstOrNull()

    val continueProgress =
        if (continueBook != null) {
            viewModel.progress.value[continueBook.id] ?: 0f
        } else {
            0f
        }

    // =========================================================
    // LOADING
    // =========================================================

    if (isLoading && books.isEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            CircularProgressIndicator()

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Китаплар йөкләнә...",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        return
    }

    // =========================================================
    // ERROR
    // =========================================================

    if (error != null && books.isEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Китапларны йөкләп булмады",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = error ?: "",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            PrimaryButton(
                text = "Кабатлап карау",
                onClick = {
                    viewModel.loadBooks()
                }
            )
        }

        return
    }

    // =========================================================
    // EMPTY
    // =========================================================

    if (books.isEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Китаплар юк",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        }

        return
    }

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(horizontal = 30.dp)
    ) {

        // =====================================================
        // HEADER
        // =====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.Top
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Исәнме!",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Укуны дәвам итәбезме?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            /*
             * Языковой переключатель пока визуальный.
             *
             * Реальное переключение языка интерфейса
             * сейчас не является частью backend-логики.
             */
            LanguageToggle(
                language = AppLanguage.TAT,
                onToggle = {}
            )
        }

        // =====================================================
        // CONTINUE READING
        // =====================================================

        if (continueBook != null) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .clip(AppShapes.large)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable {
                        navController.navigate(
                            "book_detail/${continueBook.id}"
                        )
                    }
                    .padding(20.dp)
            ) {

                Text(
                    text = "Киләсе адым",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = continueBook.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Text(
                    text = continueBook.author,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (continueProgress > 0f) {

                    LinearProgressIndicator(
                        progress = {
                            continueProgress
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .clip(AppShapes.extraSmall),
                        trackColor =
                            MaterialTheme.colorScheme.surface,
                        color =
                            MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    PrimaryButton(
                        text = "Укырга",
                        onClick = {
                            navController.navigate(
                                "book_reader/${continueBook.id}"
                            )
                        }
                    )

                    Text(
                        text = "Татарча уку",
                        style =
                            MaterialTheme.typography.bodyLarge,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // =====================================================
        // MY BOOKS
        // =====================================================

        SectionHeader(
            title = "Минем китапларым",
            actionLabel = "Барысын карау",
            onActionClick = {
                navController.navigate("search")
            },
            modifier = Modifier.padding(
                top = 34.dp,
                bottom = 14.dp
            )
        )

        LazyRow(
            horizontalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            items(
                items = books,
                key = { book ->
                    book.id
                }
            ) { book ->

                BookGridCard(
                    book = book,
                    onClick = {
                        navController.navigate(
                            "book_detail/${book.id}"
                        )
                    }
                )
            }
        }

        // =====================================================
        // RECOMMENDATIONS
        // =====================================================

        SectionHeader(
            title = "Тәкъдим итәбез",
            actionLabel = "Барысын карау",
            onActionClick = {
                navController.navigate("search")
            },
            modifier = Modifier.padding(
                top = 34.dp,
                bottom = 14.dp
            )
        )

        LazyRow(
            horizontalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            items(
                items = books.reversed(),
                key = { book ->
                    "recommendation_${book.id}"
                }
            ) { book ->

                BookGridCard(
                    book = book,
                    onClick = {
                        navController.navigate(
                            "book_detail/${book.id}"
                        )
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )
    }
}
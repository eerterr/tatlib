package com.tatlib.app.ui.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.PrimaryButton
import com.tatlib.app.ui.components.ScreenTopBar

@Composable
fun BookDetailScreen(
    navController: NavHostController,
    viewModel: AppViewModel,
    bookId: String
) {
    val selectedBook by viewModel.selectedBook.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(bookId) {
        val id = bookId.toIntOrNull()

        if (id != null) {
            viewModel.loadBook(id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp)
    ) {
        ScreenTopBar(
            onBack = {
                navController.popBackStack()
            },
            showLanguageToggle = false,
            modifier = Modifier.padding(
                top = 16.dp,
                bottom = 8.dp
            )
        )

        if (isLoading && selectedBook == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()

                Text(
                    text = "Китап йөкләнә...",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            return@Column
        }

        if (error != null && selectedBook == null) {
            Text(
                text = error ?: "Китапны йөкләп булмады",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 32.dp)
            )

            return@Column
        }

        val book = selectedBook ?: run {
            Text(
                text = "Китап табылмады",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 32.dp)
            )

            return@Column
        }

        Text(
            text = book.title,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(
                top = 20.dp,
                bottom = 8.dp
            )
        )

        Text(
            text = book.author,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (book.genre.isNotBlank()) {
            Text(
                text = book.genre,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Text(
            text = "Дәрәҗә: ${book.level.label}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (book.description.isNotBlank()) {
            Text(
                text = book.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (book.readingTimeLabel.isNotBlank()) {
                Text(
                    text = book.readingTimeLabel,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "${book.pageCount} бүлек",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        PrimaryButton(
            text = "Укый башлау",
            onClick = {
                navController.navigate("book_reader/${book.id}")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
    }
}
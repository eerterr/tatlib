package com.tatlib.app.ui.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.data.Book
import com.tatlib.app.data.MockData
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.BookHeroCover
import com.tatlib.app.ui.components.PrimaryButton
import com.tatlib.app.ui.components.ScreenTopBar
import com.tatlib.app.ui.components.StatTile
import com.tatlib.app.ui.components.TagChip
import com.tatlib.app.ui.navigation.Routes

@Composable
fun BookDetailScreen(navController: NavHostController, viewModel: AppViewModel, bookId: String) {
    val book: Book = MockData.bookById(bookId) ?: MockData.shurale

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp)
    ) {
        ScreenTopBar(
            onBack = { navController.popBackStack() },
            showLanguageToggle = false,
            showOverflow = true,
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            BookHeroCover(book)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    book.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Light
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    book.tags.forEach { tag -> TagChip(tag) }
                }
            }
        }

        PrimaryButton(
            text = "Укырга",
            onClick = { navController.navigate(Routes.bookReader(book.id)) },
            modifier = Modifier.padding(top = 20.dp)
        )

        Text(
            "Китап турында",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 30.dp, bottom = 10.dp)
        )
        Text(book.description, style = MaterialTheme.typography.bodyLarge)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, bottom = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatTile(value = book.pageCount.toString(), label = "текст")
            StatTile(value = book.level.label, label = "дәрәҗә")
            StatTile(value = book.readingTimeLabel, label = "вакыт")
        }
    }
}

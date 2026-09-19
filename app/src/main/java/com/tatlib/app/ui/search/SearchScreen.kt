package com.tatlib.app.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.data.MockData
import com.tatlib.app.ui.components.BookListRow
import com.tatlib.app.ui.components.FilterChip
import com.tatlib.app.ui.components.QuoteCard
import com.tatlib.app.ui.navigation.Routes

private val filters = listOf("Барысы", "Китаплар", "Авторлар", "Темалар")

@Composable
fun SearchScreen(navController: NavHostController) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(filters.first()) }

    val results = MockData.allBooks.filter { book ->
        query.isBlank() ||
            book.title.contains(query, ignoreCase = true) ||
            book.author.contains(query, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        item {
            Text(
                "Эзләү",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 24.dp, bottom = 18.dp)
            )
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Китап, автор яки тема буенча эзләгез...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.padding(top = 14.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        text = filter,
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }
        }

        items(results) { book ->
            BookListRow(
                book = book,
                onClick = { navController.navigate(Routes.bookDetail(book.id)) },
                modifier = Modifier.padding(bottom = 14.dp)
            )
        }

        item {
            if (results.isEmpty()) {
                Text(
                    "Бернәрсә дә табылмады",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            QuoteCard(
                text = MockData.inspirationalQuotes.first(),
                modifier = Modifier.padding(bottom = 30.dp)
            )
        }
    }
}

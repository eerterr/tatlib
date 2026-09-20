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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.data.MockData
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.BookListRow
import com.tatlib.app.ui.components.FilterChip
import com.tatlib.app.ui.components.QuoteCard
import com.tatlib.app.ui.navigation.Routes

private val filters = listOf(
    "Барысы",
    "Китаплар",
    "Авторлар",
    "Темалар"
)

@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    var query by remember {
        mutableStateOf("")
    }

    var selectedFilter by remember {
        mutableStateOf(filters.first())
    }

    val books by viewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val results = books.filter { book ->
        if (query.isBlank()) {
            true
        } else {
            book.title.contains(
                query,
                ignoreCase = true
            ) ||
                    book.author.contains(
                        query,
                        ignoreCase = true
                    )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {

        item {
            Text(
                text = "Эзләү",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(
                    top = 24.dp,
                    bottom = 18.dp
                )
            )

            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                },
                placeholder = {
                    Text(
                        "Китап, автор яки тема буенча эзләгез..."
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.padding(
                    top = 14.dp,
                    bottom = 20.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        text = filter,
                        selected = selectedFilter == filter,
                        onClick = {
                            selectedFilter = filter
                        }
                    )
                }
            }
        }

        if (isLoading) {

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Китаплар йөкләнә..."
                    )
                }
            }

        } else if (error != null && books.isEmpty()) {

            item {
                Text(
                    text = error ?: "Китапларны йөкләп булмады",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(
                        vertical = 24.dp
                    )
                )
            }

        } else if (results.isEmpty()) {

            item {
                Text(
                    text = "Бернәрсә дә табылмады",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        vertical = 24.dp
                    )
                )
            }

        } else {

            items(
                items = results,
                key = { it.id }
            ) { book ->

                BookListRow(
                    book = book,
                    onClick = {
                        navController.navigate(
                            Routes.bookDetail(book.id)
                        )
                    },
                    modifier = Modifier.padding(
                        bottom = 14.dp
                    )
                )
            }
        }

        item {
            Spacer(
                modifier = Modifier.height(10.dp)
            )

            if (MockData.inspirationalQuotes.isNotEmpty()) {
                QuoteCard(
                    text = MockData.inspirationalQuotes.first(),
                    modifier = Modifier.padding(
                        bottom = 30.dp
                    )
                )
            }
        }
    }
}
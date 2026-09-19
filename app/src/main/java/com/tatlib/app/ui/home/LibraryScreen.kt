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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.data.MockData
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.BookGridCard
import com.tatlib.app.ui.components.LanguageToggle
import com.tatlib.app.ui.components.PrimaryButton
import com.tatlib.app.ui.components.SectionHeader
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.AppShapes

@Composable
fun LibraryScreen(navController: NavHostController, viewModel: AppViewModel) {
    val continueBook = MockData.bookById(viewModel.continueReadingBookId) ?: MockData.shurale
    val continueProgress = viewModel.progressFor(continueBook)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    "Исәнме, ${viewModel.userName}!",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Укуны дәвам итәбезме?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LanguageToggle(language = viewModel.language, onToggle = viewModel::toggleLanguage)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .clip(AppShapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { navController.navigate(Routes.bookDetail(continueBook.id)) }
                .padding(20.dp)
        ) {
            Text(
                "Киләсе адым",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                continueBook.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                continueBook.author,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (continueProgress > 0f) {
                LinearProgressIndicator(
                    progress = { continueProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                        .clip(AppShapes.extraSmall),
                    trackColor = MaterialTheme.colorScheme.surface,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PrimaryButton(
                    text = "Укырга",
                    onClick = { navController.navigate(Routes.bookReader(continueBook.id)) }
                )
                Text(
                    "+ 10% Катлаулылык",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SectionHeader(
            title = "Минем китапларым",
            actionLabel = "Барысын карау",
            onActionClick = { navController.navigate(Routes.SEARCH) },
            modifier = Modifier.padding(top = 34.dp, bottom = 14.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(MockData.allBooks) { book ->
                BookGridCard(
                    book = book,
                    onClick = { navController.navigate(Routes.bookDetail(book.id)) }
                )
            }
        }

        SectionHeader(
            title = "Тәкъдим итәбез",
            actionLabel = "Барысын карау",
            onActionClick = { navController.navigate(Routes.SEARCH) },
            modifier = Modifier.padding(top = 34.dp, bottom = 14.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(MockData.allBooks.reversed()) { book ->
                BookGridCard(
                    book = book,
                    onClick = { navController.navigate(Routes.bookDetail(book.id)) }
                )
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}

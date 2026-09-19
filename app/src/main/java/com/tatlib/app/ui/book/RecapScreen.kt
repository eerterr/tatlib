package com.tatlib.app.ui.book

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.data.MockData
import com.tatlib.app.ui.components.PrimaryButton
import com.tatlib.app.ui.components.ScreenTopBar
import com.tatlib.app.ui.components.SoftCard
import com.tatlib.app.ui.components.StatTile
import com.tatlib.app.ui.navigation.Routes

@Composable
fun RecapScreen(navController: NavHostController, bookId: String) {
    val book = MockData.bookById(bookId) ?: MockData.shurale
    val stats = MockData.progressStats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        ScreenTopBar(
            onBack = { navController.popBackStack() },
            showLanguageToggle = false,
            modifier = Modifier.padding(top = 16.dp)
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(
                "Һәр укылган бит сине оригиналга якынайта!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 28.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatTile(value = "+${stats.newWords}", label = "яңа сүз")
                StatTile(value = stats.textsRead.toString(), label = "укылган текст")
                StatTile(value = "${stats.originalTextPercent}%", label = "оригинал текст")
            }

            Spacer(Modifier.height(28.dp))

            SoftCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 14.dp)
                    )
                    Column {
                        Text("Уку — үсеш", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Һәр укылган бит — татар телен яхшырак аңлауга таба бер адым",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        PrimaryButton(
            text = "Укырга",
            onClick = { navController.navigate(Routes.bookDetail(book.id)) },
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            "Китапханәгә кайту",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate(Routes.LIBRARY) {
                        popUpTo(Routes.LIBRARY) { inclusive = true }
                    }
                }
                .padding(vertical = 6.dp)
                .padding(bottom = 30.dp)
        )
    }
}

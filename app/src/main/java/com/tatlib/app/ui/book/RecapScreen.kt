package com.tatlib.app.ui.book

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.R
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.PhotoHero
import com.tatlib.app.ui.components.ReadPill
import com.tatlib.app.ui.components.StatValue
import com.tatlib.app.ui.components.TextLink
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.Ornaments
import com.tatlib.app.ui.theme.tatlibColors

/**
 * Итог сессии (Recap.dc.html): кремль сверху, «×», заголовок, три «—» (сессий в базе нет,
 * 00-ux-map п. 16), карточка с тюльпаном, «Укырга» → ридер, «Китапханәгә кайту» → библиотека.
 */
@Composable
fun RecapScreen(navController: NavHostController, viewModel: AppViewModel, bookId: String) {
    val book = viewModel.getBook(bookId)
    val scheme = MaterialTheme.colorScheme
    val colors = MaterialTheme.tatlibColors
    val toLibrary = {
        navController.navigate(Routes.LIBRARY) {
            popUpTo(Routes.LIBRARY) { inclusive = true }
        }
    }

    Box(Modifier.fillMaxSize().background(scheme.background)) {
        PhotoHero(
            painter = painterResource(R.drawable.kremlin_bottom),
            height = 320.dp,
            overlay = Brush.verticalGradient(
                0f to scheme.background.copy(alpha = 0.15f),
                0.55f to scheme.background.copy(alpha = 0.75f),
                1f to scheme.background
            )
        )
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            // TopBar не даёт фона кнопке, а в макете «×» стоит на белом круге 60 % — своя кнопка тех же 48 dp.
            Box(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 16.dp)) {
                IconButton(
                    onClick = toLibrary,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(48.dp)
                        .background(colors.onPhoto.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.action_close),
                        tint = scheme.scrim
                    )
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 150.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Text(
                    stringResource(R.string.recap_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = scheme.onBackground
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatValue("—", stringResource(R.string.recap_stat_pages))
                    StatValue("—", stringResource(R.string.recap_stat_words))
                    StatValue("—", stringResource(R.string.recap_stat_original))
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(scheme.surface, MaterialTheme.shapes.medium)
                        .border(1.dp, colors.line, MaterialTheme.shapes.medium)
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Ornaments.Tulip, contentDescription = null, tint = colors.forest, modifier = Modifier.size(40.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            stringResource(R.string.recap_card_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = scheme.onSurface
                        )
                        Text(
                            stringResource(R.string.recap_card_text),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.inkSoft
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                ReadPill(
                    text = stringResource(R.string.action_read),
                    onClick = {
                        if (book != null) {
                            // Не копить пары reader → recap в стеке.
                            navController.navigate(Routes.bookReader(book.id)) { popUpTo(Routes.BOOK_READER) { inclusive = true } }
                        } else toLibrary()
                    },
                    fullWidth = true
                )
                TextLink(
                    text = stringResource(R.string.recap_back_to_library),
                    onClick = toLibrary,
                    fullWidth = true
                )
            }
        }
    }
}

package com.tatlib.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.R
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.ActionButton
import com.tatlib.app.ui.components.ActionStyle
import com.tatlib.app.ui.components.AppLanguage
import com.tatlib.app.ui.components.FullPhotoBackground
import com.tatlib.app.ui.components.LanguageToggle
import com.tatlib.app.ui.components.OnPhotoLink
import com.tatlib.app.ui.components.TopBar
import com.tatlib.app.ui.components.TopBarLeft
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.tatlibColors

/**
 * Welcome (`Welcome.dc.html` / `WelcomeRu.dc.html`): фото на весь экран, логотип и TAT/RU в шапке,
 * внизу заголовок, подзаголовок, круг «Башлау» и ссылка «Трендлар».
 * Русские тексты приходят из `values-ru` по локали системы; переключатель — визуальный no-op
 * (00-ux-map п. 21).
 */
@Composable
fun WelcomeScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    var language by remember { mutableStateOf(AppLanguage.TAT) }
    val scrim = MaterialTheme.colorScheme.scrim
    val onPhoto = MaterialTheme.tatlibColors.onPhoto

    Box(Modifier.fillMaxSize()) {
        FullPhotoBackground(
            painter = painterResource(R.drawable.hero_blossom),
            overlay = Brush.verticalGradient(0.4f to scrim.copy(alpha = 0f), 1f to scrim.copy(alpha = 0.62f)),
            contentAlignment = BiasAlignment(0f, -0.2f) // center 40 %
        )
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopBar(
                left = TopBarLeft.Logo,
                onPhoto = true,
                right = {
                    LanguageToggle(
                        language = language,
                        onToggle = { language = if (language == AppLanguage.TAT) AppLanguage.RU else AppLanguage.TAT },
                        onPhoto = true
                    )
                }
            )
            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(stringResource(R.string.welcome_title), style = MaterialTheme.typography.headlineLarge, color = onPhoto)
                Text(
                    stringResource(R.string.welcome_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = onPhoto.copy(alpha = 0.92f),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionButton(
                        text = stringResource(R.string.welcome_start),
                        onClick = { navController.navigate(Routes.AUTH_CHOICE) },
                        style = ActionStyle.OnPhoto
                    )
                    // В макете ссылка ведёт в никуда; внутренний отступ ссылки 16 dp компенсируем offset,
                    // чтобы текст стоял у правого края, как в макете.
                    OnPhotoLink(
                        text = stringResource(R.string.welcome_trends),
                        onClick = {},
                        modifier = Modifier.offset(x = 16.dp)
                    )
                }
            }
        }
    }
}

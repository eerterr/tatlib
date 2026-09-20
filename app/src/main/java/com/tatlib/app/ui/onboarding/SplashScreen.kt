package com.tatlib.app.ui.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.tatlib.app.ui.components.FullPhotoBackground
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.Ornaments
import com.tatlib.app.ui.theme.tatlibColors
import kotlinx.coroutines.delay

/**
 * Splash (`Main.dc.html`): фото на весь экран, лилия + «TatLib» + «Татарча күбрәк», внизу индикатор.
 * Сессии в прототипе нет, поэтому через 650 мс всегда уходим на Welcome; проверка сохранённой
 * сессии (LIBRARY / BOOK_READER) подставится сюда вместо безусловного перехода.
 */
@Composable
fun SplashScreen(navController: NavHostController, viewModel: AppViewModel) {
    var progress by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "splashProgress"
    )

    LaunchedEffect(Unit) {
        progress = 1f
        delay(650)
        navController.navigate(Routes.ONBOARDING_WELCOME) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    val scrim = MaterialTheme.colorScheme.scrim
    val onPhoto = MaterialTheme.tatlibColors.onPhoto
    Box(Modifier.fillMaxSize()) {
        FullPhotoBackground(
            painter = painterResource(R.drawable.hero_blossom),
            overlay = Brush.verticalGradient(listOf(scrim.copy(alpha = 0.05f), scrim.copy(alpha = 0.55f))),
            contentAlignment = BiasAlignment(0f, -0.4f) // center 30 %
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Ornaments.Lily, contentDescription = null, tint = onPhoto, modifier = Modifier.size(56.dp))
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge, color = onPhoto)
            Text(
                stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.displaySmall,
                color = onPhoto.copy(alpha = 0.9f)
            )
        }
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 56.dp)
                .size(28.dp),
            color = onPhoto,
            strokeWidth = 3.dp,
            trackColor = onPhoto.copy(alpha = 0.35f)
        )
    }
}

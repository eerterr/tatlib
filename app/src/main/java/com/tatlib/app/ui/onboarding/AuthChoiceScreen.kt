package com.tatlib.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.R
import com.tatlib.app.ui.components.ActionButton
import com.tatlib.app.ui.components.ActionStyle
import com.tatlib.app.ui.components.AppLanguage
import com.tatlib.app.ui.components.FullPhotoBackground
import com.tatlib.app.ui.components.LanguageToggle
import com.tatlib.app.ui.components.TopBar
import com.tatlib.app.ui.components.TopBarLeft
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.Ornaments
import com.tatlib.app.ui.theme.tatlibColors

/**
 * Выбор входа (`AuthChoice.dc.html`): закат на весь экран, «назад» и TAT/RU в шапке,
 * внизу вопрос, пояснение, круг «Керү» (белый) и справа круг «Теркәлү» (sky2).
 */
@Composable
fun AuthChoiceScreen(navController: NavHostController) {
    var language by remember { mutableStateOf(AppLanguage.TAT) }
    val scrim = MaterialTheme.colorScheme.scrim
    val onPhoto = MaterialTheme.tatlibColors.onPhoto

    Box(Modifier.fillMaxSize()) {
        FullPhotoBackground(
            painter = painterResource(R.drawable.hero_sunset),
            overlay = Brush.verticalGradient(listOf(onPhoto.copy(alpha = 0.1f), scrim.copy(alpha = 0.7f)))
        )
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopBar(
                left = TopBarLeft.Back,
                onLeft = { navController.popBackStack() },
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
                    .padding(start = 24.dp, end = 24.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(stringResource(R.string.auth_title), style = MaterialTheme.typography.headlineLarge, color = onPhoto)
                Text(
                    stringResource(R.string.auth_text),
                    style = MaterialTheme.typography.bodyLarge,
                    color = onPhoto.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                ActionButton(
                    text = stringResource(R.string.auth_login),
                    onClick = { navController.navigate(Routes.LOGIN) },
                    style = ActionStyle.OnPhoto
                )
                SecondaryOnPhotoAction(
                    text = stringResource(R.string.auth_register),
                    onClick = { navController.navigate(Routes.REGISTER) },
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

/**
 * `action("Теркәлү", color=sky2, align=right, light=True)`: круг sky2 с узором 1, подпись белая с тенью
 * справа налево. `ActionButton(Secondary)` даёт подпись цветом ink без тени, поэтому — приватный вариант.
 */
@Composable
private fun SecondaryOnPhotoAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.tatlibColors
    val blur = with(LocalDensity.current) { 10.dp.toPx() }
    val labelStyle = MaterialTheme.typography.titleMedium.copy(
        shadow = Shadow(MaterialTheme.colorScheme.scrim.copy(alpha = 0.35f), Offset(0f, 1f), blur)
    )
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .heightIn(min = 56.dp)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = labelStyle, color = colors.onPhoto, textAlign = TextAlign.End)
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(6.dp, CircleShape, ambientColor = colors.shadow, spotColor = colors.shadow)
                .background(colors.sky2, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Ornaments.Curl, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(24.dp))
        }
    }
}

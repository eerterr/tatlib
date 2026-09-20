package com.tatlib.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.R
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.ActionButton
import com.tatlib.app.ui.components.ActionStyle
import com.tatlib.app.ui.components.AppLanguage
import com.tatlib.app.ui.components.FullPhotoBackground
import com.tatlib.app.ui.components.GlassCard
import com.tatlib.app.ui.components.GlassField
import com.tatlib.app.ui.components.LanguageToggle
import com.tatlib.app.ui.components.OnPhotoLink
import com.tatlib.app.ui.components.TopBar
import com.tatlib.app.ui.components.TopBarLeft
import com.tatlib.app.ui.navigation.Routes

/**
 * Вход (`Login.dc.html`): то же фото, заголовок «Керү» ниже (120 dp), стеклянная карточка с двумя
 * полями, ссылка на регистрацию, внизу справа круг «Керү». Backend-auth нет: переход на LEVEL_INTRO.
 */
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    var language by remember { mutableStateOf(AppLanguage.TAT) }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val canSubmit = login.isNotBlank() && password.isNotBlank()

    Box(Modifier.fillMaxSize()) {
        FullPhotoBackground(
            painter = painterResource(R.drawable.hero_blossom),
            overlay = SolidColor(MaterialTheme.colorScheme.scrim.copy(alpha = 0f)), // в макете градиента нет
            contentAlignment = BiasAlignment(0f, -0.3f) // center 35 %
        )
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 120.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    stringResource(R.string.login_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                GlassCard {
                    GlassField(
                        value = login,
                        onValueChange = { login = it },
                        placeholder = stringResource(R.string.login_contact),
                        leadingIcon = Icons.Rounded.Email,
                        keyboardType = KeyboardType.Email
                    )
                    GlassField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = stringResource(R.string.field_password),
                        leadingIcon = Icons.Rounded.Lock,
                        trailingIcon = if (showPassword) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                        onTrailingClick = { showPassword = !showPassword },
                        isPassword = !showPassword,
                        keyboardType = KeyboardType.Password
                    )
                }
                // Внутренний отступ ссылки 16 dp компенсируем offset, чтобы текст стоял у левого края.
                OnPhotoLink(
                    text = stringResource(R.string.login_no_account),
                    onClick = { navController.navigate(Routes.REGISTER) },
                    modifier = Modifier.offset(x = (-16).dp)
                )
            }
            // ActionButton без enabled: при пустых полях круг приглушён, нажатие — no-op.
            ActionButton(
                text = stringResource(R.string.auth_login),
                onClick = {
                    if (canSubmit) {
                        navController.navigate(Routes.LEVEL_INTRO) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                style = ActionStyle.OnPhoto,
                alignEnd = true,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(start = 24.dp, end = 24.dp, top = 18.dp, bottom = 24.dp)
                    .alpha(if (canSubmit) 1f else 0.5f)
            )
        }
    }
}

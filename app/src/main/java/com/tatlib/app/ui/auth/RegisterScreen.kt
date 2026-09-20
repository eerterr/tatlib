package com.tatlib.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
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
import com.tatlib.app.ui.components.GlassChip
import com.tatlib.app.ui.components.GlassField
import com.tatlib.app.ui.components.LanguageToggle
import com.tatlib.app.ui.components.TopBar
import com.tatlib.app.ui.components.TopBarLeft
import com.tatlib.app.ui.navigation.Routes

private val ageBrackets = listOf(
    R.string.register_age_10_14,
    R.string.register_age_14_18,
    R.string.register_age_18_25,
    R.string.register_age_25
)

/**
 * Регистрация (`Register.dc.html`): фото без градиента, логотип и TAT/RU, заголовок «Теркәлү»,
 * стеклянная карточка с четырьмя полями, чипы возраста, внизу справа круг «Дәвам итү».
 * Регистрация пока локальная (backend-auth нет): валидация как раньше, переход на LEVEL_INTRO.
 */
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    var language by remember { mutableStateOf(AppLanguage.TAT) }
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showPasswordConfirm by remember { mutableStateOf(false) }
    var selectedAge by remember { mutableStateOf(R.string.register_age_18_25) }

    val passwordsMismatch = passwordConfirm.isNotEmpty() && password != passwordConfirm
    val canSubmit = name.isNotBlank() && contact.isNotBlank() && password.length >= 4 && !passwordsMismatch

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
                    .padding(start = 24.dp, end = 24.dp, top = 60.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    stringResource(R.string.register_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                GlassCard {
                    GlassField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = stringResource(R.string.register_name),
                        leadingIcon = Icons.Rounded.Person
                    )
                    GlassField(
                        value = contact,
                        onValueChange = { contact = it },
                        placeholder = stringResource(R.string.register_contact),
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
                    GlassField(
                        value = passwordConfirm,
                        onValueChange = { passwordConfirm = it },
                        placeholder = stringResource(R.string.register_password_repeat),
                        leadingIcon = Icons.Rounded.Lock,
                        trailingIcon = if (showPasswordConfirm) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                        onTrailingClick = { showPasswordConfirm = !showPasswordConfirm },
                        isPassword = !showPasswordConfirm,
                        keyboardType = KeyboardType.Password
                    )
                    if (passwordsMismatch) {
                        Text(
                            stringResource(R.string.register_password_mismatch),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 18.dp)
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ageBrackets.forEach { bracket ->
                        GlassChip(
                            text = stringResource(bracket),
                            selected = selectedAge == bracket,
                            onClick = { selectedAge = bracket }
                        )
                    }
                }
            }
            // ActionButton без enabled: при невалидной форме круг приглушён, нажатие — no-op.
            ActionButton(
                text = stringResource(R.string.register_continue),
                onClick = {
                    // Пока регистрация локальная; возраст остаётся внутри экрана, backend-auth нет.
                    if (canSubmit) {
                        navController.navigate(Routes.LEVEL_INTRO) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
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

package com.tatlib.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tatlib.app.R
import com.tatlib.app.ui.theme.Ornaments
import com.tatlib.app.ui.theme.tatlibColors

/** Язык интерфейса для переключателя TAT / RU. */
enum class AppLanguage { TAT, RU }

/** Что стоит слева в шапке `.top`. */
enum class TopBarLeft { Back, Close, Logo, None }

/**
 * Шапка `.top` 56 dp: слева «назад» / «закрыть» / росток-логотип, по центру необязательный заголовок,
 * справа слот (LanguageToggle, IconButton). `onPhoto` — всё белое.
 * Где используется: все экраны, кроме поиска и прогресса (там заголовок h0 вместо шапки).
 */
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    left: TopBarLeft = TopBarLeft.Back,
    onLeft: () -> Unit = {},
    right: @Composable () -> Unit = {},
    onPhoto: Boolean = false,
    title: String? = null
) {
    val tint = if (onPhoto) MaterialTheme.tatlibColors.onPhoto else MaterialTheme.colorScheme.onBackground
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp)
    ) {
        Box(Modifier.align(Alignment.CenterStart).size(48.dp), contentAlignment = Alignment.Center) {
            when (left) {
                TopBarLeft.Back -> IconButton(onClick = onLeft, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.action_back), tint = tint)
                }
                TopBarLeft.Close -> IconButton(onClick = onLeft, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.action_close), tint = tint)
                }
                TopBarLeft.Logo -> Icon(
                    Ornaments.Lily,
                    contentDescription = stringResource(R.string.cd_logo),
                    tint = tint,
                    modifier = Modifier.size(28.dp)
                )
                TopBarLeft.None -> Unit
            }
        }
        if (title != null) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = tint,
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 56.dp)
            )
        }
        Row(Modifier.align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) { right() }
    }
}

/**
 * Переключатель языка `.lang`: два labelLarge через « / », активный с подчёркиванием 2 dp terracotta
 * (на фото — белое), неактивный inkMuted (на фото — белый 70 %).
 * Где используется: онбординг, вход, регистрация, главная, профиль.
 */
@Composable
fun LanguageToggle(
    language: AppLanguage,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onPhoto: Boolean = false
) {
    val colors = MaterialTheme.tatlibColors
    val active = if (onPhoto) colors.onPhoto else MaterialTheme.colorScheme.onBackground
    val inactive = if (onPhoto) colors.onPhoto.copy(alpha = 0.7f) else colors.inkMuted
    val underline = if (onPhoto) colors.onPhoto else colors.terracotta
    val label = stringResource(R.string.lang_switch_label)
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .heightIn(min = 48.dp)
            .semantics { contentDescription = label }
            .clickable(interactionSource = interaction, indication = null, onClick = onToggle)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LangWord(stringResource(R.string.lang_tat), language == AppLanguage.TAT, active, inactive, underline)
        Spacer(Modifier.size(6.dp))
        Text("/", style = MaterialTheme.typography.labelLarge, color = inactive)
        Spacer(Modifier.size(6.dp))
        LangWord(stringResource(R.string.lang_ru), language == AppLanguage.RU, active, inactive, underline)
    }
}

@Composable
private fun LangWord(text: String, on: Boolean, active: Color, inactive: Color, underline: Color) {
    val thickness = with(LocalDensity.current) { 2.dp.toPx() }
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = if (on) active else inactive,
        modifier = Modifier
            .padding(bottom = 2.dp)
            .drawBehind {
                if (on) {
                    val y = size.height + thickness / 2
                    drawLine(underline, Offset(0f, y), Offset(size.width, y), thickness)
                }
            }
    )
}

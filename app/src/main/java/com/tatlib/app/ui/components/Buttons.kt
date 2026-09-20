package com.tatlib.app.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tatlib.app.ui.theme.Ornaments
import com.tatlib.app.ui.theme.PillShape
import com.tatlib.app.ui.theme.tatlibColors

/** Цвет круга единого компонента перехода (MASTER § 0 v2.1, `action()` в gen2.py). */
enum class ActionStyle { Primary, Secondary, OnPhoto }

/**
 * Единый компонент перехода `.act`: круг 56 dp с узором 1 и подпись рядом.
 * Где используется: онбординг, тест уровня, главная («Укуны дәвам итү»), регистрация, вход.
 */
@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    style: ActionStyle,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false
) {
    val colors = MaterialTheme.tatlibColors
    val circle = when (style) {
        ActionStyle.Primary -> colors.peach2
        ActionStyle.Secondary -> colors.sky2
        ActionStyle.OnPhoto -> colors.onPhoto.copy(alpha = 0.92f)
    }
    // На белом круге узор всегда тёмный (scrim = ink / night), иначе — «чернила» темы.
    val iconTint = if (style == ActionStyle.OnPhoto) MaterialTheme.colorScheme.scrim else MaterialTheme.colorScheme.onBackground
    val labelColor = if (style == ActionStyle.OnPhoto) colors.onPhoto else MaterialTheme.colorScheme.onBackground
    val blur = with(LocalDensity.current) { 10.dp.toPx() }
    val labelStyle = if (style == ActionStyle.OnPhoto) {
        MaterialTheme.typography.titleMedium.copy(shadow = Shadow(Color.Black.copy(alpha = 0.35f), Offset(0f, 1f), blur))
    } else {
        MaterialTheme.typography.titleMedium
    }
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .heightIn(min = 56.dp)
            .pressScale(interaction)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (alignEnd) {
            Text(text, style = labelStyle, color = labelColor, textAlign = TextAlign.End)
        }
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(6.dp, CircleShape, ambientColor = colors.shadow, spotColor = colors.shadow)
                .background(circle, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Ornaments.Curl, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        if (!alignEnd) {
            Text(text, style = labelStyle, color = labelColor)
        }
    }
}

/**
 * Круглая кнопка `.rb`: узор 1 по умолчанию, любая иконка на замену.
 * Где используется: мини-бар (40 dp, sage2, PlayArrow), затвор камеры сканера (72 dp, рамка 4 dp sage2 — задаёт вызывающий через `modifier.border`).
 */
@Composable
fun RoundButton(
    onClick: () -> Unit,
    contentDescription: String?,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    icon: ImageVector = Ornaments.Curl,
    iconSize: Dp = 22.dp
) {
    val shadow = MaterialTheme.tatlibColors.shadow
    Box(
        modifier = modifier
            .size(size)
            .shadow(6.dp, CircleShape, ambientColor = shadow, spotColor = shadow)
            .background(color, CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = contentColor, modifier = Modifier.size(iconSize))
    }
}

/**
 * Пилюля «читать» `.btn-sage`: 56 dp, sage2, orb 28 dp с ▶ слева.
 * Где используется: BookDetail «Укырга», Recap, лист «Китап табылды» («Тулы версиясен укырга»).
 */
@Composable
fun ReadPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fullWidth: Boolean = false
) {
    val scheme = MaterialTheme.colorScheme
    // tatlib.css: текст #1D3128 в обеих темах → светлая: ink, тёмная: Night (onPrimary).
    val content = if (scheme.isDark) scheme.onPrimary else scheme.onBackground
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .height(56.dp)
            .pressScale(interaction)
            .clip(PillShape)
            .background(MaterialTheme.tatlibColors.sage2)
            .clickable(interactionSource = interaction, indication = LocalIndication.current, onClick = onClick)
            .padding(start = 16.dp, end = 26.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(28.dp).background(content.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = content, modifier = Modifier.size(18.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge, color = content)
    }
}

/**
 * Текстовая ссылка `.btn-ghost`: 48 dp, без фона, ink, подчёркивание 1.5 dp `sage` с отступом.
 * Где используется: «Ябарга» во всплывашке, «Китапханәгә кайту» на Recap, «Галерея» в сканере, «Чыгу» в профиле.
 */
@Composable
fun TextLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    fullWidth: Boolean = false
) {
    UnderlinedLink(
        text = text,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        fullWidth = fullWidth,
        textColor = MaterialTheme.colorScheme.onBackground,
        lineColor = MaterialTheme.tatlibColors.sage,
        style = MaterialTheme.typography.labelLarge,
        height = 48.dp
    )
}

/**
 * Белая ссылка на фото с подчёркиванием (`text-decoration:underline` + `color:#fff`).
 * Где используется: Welcome «Трендлар», Login «Исәп язмасы юк — теркәлү», главная «Трендлар».
 */
@Composable
fun OnPhotoLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val white = MaterialTheme.tatlibColors.onPhoto
    val blur = with(LocalDensity.current) { 8.dp.toPx() }
    UnderlinedLink(
        text = text,
        onClick = onClick,
        modifier = modifier,
        icon = null,
        fullWidth = false,
        textColor = white,
        lineColor = white,
        style = MaterialTheme.typography.bodyMedium.copy(shadow = Shadow(Color.Black.copy(alpha = 0.3f), Offset(0f, 1f), blur)),
        height = 48.dp
    )
}

@Composable
private fun UnderlinedLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    icon: ImageVector?,
    fullWidth: Boolean,
    textColor: Color,
    lineColor: Color,
    style: TextStyle,
    height: Dp
) {
    val thickness = with(LocalDensity.current) { 1.5.dp.toPx() }
    Row(
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .height(height)
            .clip(PillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text,
            style = style,
            color = textColor,
            modifier = Modifier.drawBehind {
                // Линия под нижней кромкой строки: цвет отличается от текста, поэтому не TextDecoration.
                val y = size.height - thickness / 2
                drawLine(lineColor, Offset(0f, y), Offset(size.width, y), thickness)
            }
        )
    }
}

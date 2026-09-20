package com.tatlib.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tatlib.app.R
import com.tatlib.app.ui.theme.ArchShape
import com.tatlib.app.ui.theme.Ornaments
import com.tatlib.app.ui.theme.PillShape
import com.tatlib.app.ui.theme.SheetTopShape
import com.tatlib.app.ui.theme.tatlibColors

// ---------- стекло на фото ----------

/**
 * Стеклянная карточка `.glass`: r28, белый 34 %, рамка 55 %, padding 16, gap 12.
 * Blur не применяется (решение владельца, PROMPT-PHASE-4 § 2.1: `Modifier.blur` только с API 31).
 * Где используется: регистрация, вход.
 */
@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val colors = MaterialTheme.tatlibColors
    val shape = RoundedCornerShape(28.dp)
    val shadow = MaterialTheme.colorScheme.scrim.copy(alpha = 0.12f)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, shape, ambientColor = shadow, spotColor = shadow)
            .background(colors.glassFill, shape)
            .border(1.dp, colors.glassBorder, shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

/**
 * Поле на стекле `.gfield`: 54 dp pill, белый 55 %, рамка 70 %, иконка слева 20, плейсхолдер inkMuted.
 * Где используется: регистрация (Исем, Телефон…, Серсүз), вход.
 */
@Composable
fun GlassField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    onTrailingClick: () -> Unit = {},
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val white = MaterialTheme.tatlibColors.onPhoto
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    // focus = 2 dp secondary (MASTER § 7, .field.focus в tatlib.css)
    val stroke = if (focused) 2.dp else 1.dp
    val strokeColor = if (focused) MaterialTheme.colorScheme.secondary else white.copy(alpha = 0.7f)
    FieldFrame(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        interactionSource = interaction,
        modifier = modifier
            .height(54.dp)
            .background(MaterialTheme.tatlibColors.glassField, PillShape)
            .border(stroke, strokeColor, PillShape)
            .padding(horizontal = 18.dp),
        trailingIcon = trailingIcon,
        onTrailingClick = onTrailingClick,
        isPassword = isPassword,
        keyboardType = keyboardType
    )
}

/**
 * Обычное поле `.field` (§ 7.10): 52 dp, r14, surface, рамка line.
 * Где используется: поиск («Китап, автор яки тема…»).
 */
@Composable
fun PlainField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    onTrailingClick: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val shape = RoundedCornerShape(14.dp)
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val stroke = if (focused) 2.dp else 1.dp
    val strokeColor = if (focused) MaterialTheme.colorScheme.secondary else MaterialTheme.tatlibColors.line
    FieldFrame(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        interactionSource = interaction,
        modifier = modifier
            .height(52.dp)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(stroke, strokeColor, shape)
            .padding(horizontal = 16.dp),
        trailingIcon = trailingIcon,
        onTrailingClick = onTrailingClick,
        isPassword = false,
        keyboardType = keyboardType
    )
}

@Composable
private fun FieldFrame(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    interactionSource: MutableInteractionSource,
    modifier: Modifier,
    trailingIcon: ImageVector?,
    onTrailingClick: () -> Unit,
    isPassword: Boolean,
    keyboardType: KeyboardType
) {
    val colors = MaterialTheme.tatlibColors
    val ink = MaterialTheme.colorScheme.onBackground
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = ink),
        cursorBrush = SolidColor(ink),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        interactionSource = interactionSource,
        modifier = modifier.fillMaxWidth(),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(leadingIcon, contentDescription = null, tint = colors.inkSoft, modifier = Modifier.size(20.dp))
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = colors.inkMuted, maxLines = 1)
                    }
                    inner()
                }
                if (trailingIcon != null) {
                    IconButton(onClick = onTrailingClick, modifier = Modifier.size(40.dp)) {
                        Icon(trailingIcon, contentDescription = null, tint = colors.inkMuted, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    )
}

// ---------- фото ----------

/**
 * Фото на всю ширину сверху с градиентом `photo_hero()`. Кладётся первым в `Box` экрана.
 * Где используется: главная (400 dp, к фону), Recap (320), профиль (220).
 */
@Composable
fun PhotoHero(
    painter: Painter,
    height: Dp,
    overlay: Brush,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center
) {
    Box(modifier.fillMaxWidth().height(height)) {
        Image(painter, contentDescription = null, contentScale = ContentScale.Crop, alignment = contentAlignment, modifier = Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(overlay))
    }
}

/**
 * Фон экрана целиком: фото + градиент. Где используется: Splash, Welcome, AuthChoice, Register, Login.
 */
@Composable
fun FullPhotoBackground(
    painter: Painter,
    overlay: Brush,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center
) {
    Box(modifier.fillMaxSize()) {
        Image(painter, contentDescription = null, contentScale = ContentScale.Crop, alignment = contentAlignment, modifier = Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(overlay))
    }
}

/** Фото в арке-куполе `arch()` (ArchShape из темы). Где используется: LevelIntro (мечеть), LevelResult (кремль). */
@Composable
fun ArchPhoto(painter: Painter, modifier: Modifier = Modifier) {
    Image(painter, contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier.clip(ArchShape))
}

// ---------- декор ----------

/** Звезда-ромб `star()`. Где используется: экраны теста и прогресса, карточка фразы; позицию задаёт вызывающий через `offset`. */
@Composable
fun DecorStar(size: Dp, color: Color, modifier: Modifier = Modifier) {
    Icon(Ornaments.Star, contentDescription = null, tint = color, modifier = modifier.size(size))
}

/** Дуга `arc()`: окружность с обводкой 1.2 dp. Где используется: экраны теста и прогресса (sky2 / peach2). */
@Composable
fun DecorArc(diameter: Dp, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(diameter)) {
        drawCircle(color, style = Stroke(1.2.dp.toPx()))
    }
}

// ---------- секции, списки, состояния ----------

/**
 * Заголовок секции: titleLarge + справа ссылка bodyMedium inkSoft («Барысы»).
 * Где используется: главная («Минем китапларым», «Тәкъдим итәбез»).
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        if (actionLabel != null) {
            Box(
                modifier = Modifier.heightIn(min = 48.dp).clip(PillShape).clickable(onClick = onAction).padding(horizontal = 8.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(actionLabel, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.tatlibColors.inkSoft)
            }
        }
    }
}

/**
 * Вариант ответа `opt()`: r18, min 58 dp, bodyLarge; выбранный ink/cream с галочкой, иначе surface с рамкой line.
 * Где используется: квиз теста уровня.
 */
@Composable
fun QuizOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp)
            .clip(shape)
            .background(if (selected) scheme.onBackground else scheme.surface)
            .then(if (selected) Modifier else Modifier.border(1.dp, MaterialTheme.tatlibColors.line, shape))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) scheme.background else scheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(Icons.Rounded.Check, contentDescription = stringResource(R.string.cd_selected), tint = scheme.background, modifier = Modifier.size(20.dp))
        }
    }
}

/**
 * Пустое состояние (§ 7.20): тюльпан 96 dp line, titleLarge, bodyMedium inkSoft, слот действия.
 * Где используется: поиск без результатов, прогресс без данных.
 */
@Composable
fun EmptyState(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    action: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Ornaments.Tulip, contentDescription = null, tint = MaterialTheme.tatlibColors.line, modifier = Modifier.size(96.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.tatlibColors.inkSoft, textAlign = TextAlign.Center)
        action()
    }
}

/**
 * Нижний лист `.sheet`: surface, верх r28, ручка 32×4 line, padding 12/24/24.
 * Где используется: «Китап табылды» в сканере (вызывающий кладёт поверх затемнения).
 */
@Composable
fun BottomSheetCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shadow = MaterialTheme.tatlibColors.shadow
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, SheetTopShape, ambientColor = shadow, spotColor = shadow)
            .background(MaterialTheme.colorScheme.surface, SheetTopShape)
            .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 24.dp)
    ) {
        Box(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
                .size(32.dp, 4.dp)
                .background(MaterialTheme.tatlibColors.line, RoundedCornerShape(2.dp))
        )
        content()
    }
}

/** Тумблер `.toggle` (§ 7.22): трек sand / forest, ручка surface. Где используется: профиль («Сүз асты сызыгы»). */
@Composable
fun Toggle(checked: Boolean, onChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Switch(
        checked = checked,
        onCheckedChange = onChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.surface,
            checkedTrackColor = MaterialTheme.tatlibColors.forest,
            checkedBorderColor = Color.Transparent,
            uncheckedThumbColor = MaterialTheme.colorScheme.surface,
            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            uncheckedBorderColor = Color.Transparent
        )
    )
}

// ---------- сканер ----------

/**
 * Видоискатель `viewfinder()`: 400 dp, r24, фото/превью с затемнением 25 %, четыре белых уголка 28 dp.
 * `painter == null` — фон sand (превью камеры кладёт вызывающий в `content`).
 * Где используется: сканер (старт, распознавание).
 */
@Composable
fun Viewfinder(
    painter: Painter?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val white = MaterialTheme.tatlibColors.onPhoto
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (painter != null) {
            Image(painter, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.25f)))
        Corner(white, Modifier.align(Alignment.TopStart).padding(20.dp), 0f)
        Corner(white, Modifier.align(Alignment.TopEnd).padding(20.dp), 90f)
        Corner(white, Modifier.align(Alignment.BottomEnd).padding(20.dp), 180f)
        Corner(white, Modifier.align(Alignment.BottomStart).padding(20.dp), 270f)
        content()
    }
}

/** Уголок 28 dp, обводка 3 dp, скругление 8 dp; нарисован для верхнего левого, остальные — поворотом. */
@Composable
private fun Corner(color: Color, modifier: Modifier, rotation: Float) {
    Canvas(modifier.size(28.dp).rotate(rotation)) {
        val stroke = 3.dp.toPx()
        val r = 8.dp.toPx()
        val half = stroke / 2
        val path = Path().apply {
            moveTo(half, size.height)
            lineTo(half, r + half)
            quadraticBezierTo(half, half, r + half, half)
            lineTo(size.width, half)
        }
        drawPath(path, color, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}

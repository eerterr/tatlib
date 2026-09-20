package com.tatlib.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tatlib.app.R
import com.tatlib.app.data.Book
import com.tatlib.app.ui.theme.Ornaments
import com.tatlib.app.ui.theme.TileShape
import com.tatlib.app.ui.theme.tatlibColors

/**
 * Обложка `cover()`: фото из Figma для книг 1–3, для 4-й («Алтын әтәч») и любых других —
 * типографическая обложка на закатном фото. Кегли считаются от ширины: название w/7, автор w/17.
 * Где используется: ленты главной, строки поиска, BookDetail (190×285 r18), мини-бар (36×48 r8), лист сканера (88×124 r12).
 */
@Composable
fun BookCover(
    book: Book,
    modifier: Modifier = Modifier,
    width: Dp = 150.dp,
    height: Dp = 225.dp,
    radius: Dp = 16.dp
) {
    val shape = RoundedCornerShape(radius)
    val photo = when (book.id) {
        "1" -> R.drawable.cover_su_anasy
        "2" -> R.drawable.cover_shurale
        "3" -> R.drawable.cover_najip
        else -> null
    }
    Box(modifier.size(width, height).clip(shape)) {
        if (photo != null) {
            Image(painterResource(photo), contentDescription = book.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            TypographicCover(book, width, height)
        }
    }
}

@Composable
private fun BoxScope.TypographicCover(book: Book, width: Dp, height: Dp) {
    val white = MaterialTheme.tatlibColors.onPhoto
    val scrim = MaterialTheme.colorScheme.scrim
    val density = LocalDensity.current
    // Разрешённое вычисление кегля от ширины (как в cover(): max(12, w//7), max(6, w//17)).
    val titleSize = with(density) { maxOf(12.dp, width / 7).toSp() }
    val authorSize = with(density) { maxOf(6.dp, width / 17).toSp() }
    val ornament = maxOf(14.dp, width / 8)
    Image(painterResource(R.drawable.hero_sunset), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(scrim.copy(alpha = 0.05f), scrim.copy(alpha = 0.55f)))
        )
    )
    Icon(
        Ornaments.Diamond,
        contentDescription = null,
        tint = white.copy(alpha = 0.9f),
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = height * 0.14f)
            .size(ornament)
    )
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(start = 8.dp, end = 8.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            book.title,
            style = MaterialTheme.typography.displaySmall.copy(fontSize = titleSize, lineHeight = titleSize * 1.1f),
            color = white,
            textAlign = TextAlign.Center
        )
        Text(
            book.author.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = authorSize, lineHeight = authorSize * 1.3f, letterSpacing = authorSize / 10),
            color = white.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Карточка ленты: обложка 150×225 + название + подпись.
 * Где используется: главная («Минем китапларым», «Тәкъдим итәбез»); `subtitle` — «{author} · {level}» или автор.
 */
@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(150.dp).clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BookCover(book)
        Text(book.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.tatlibColors.inkSoft, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Строка книги `row()`: обложка 56×76 r10, название, автор, мета, шеврон.
 * Где используется: поиск («Барлык китаплар»); `meta` — «B1 · шигъри әкият · 1907».
 */
@Composable
fun BookRow(
    book: Book,
    onClick: () -> Unit,
    meta: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.tatlibColors
    Row(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BookCover(book, width = 56.dp, height = 76.dp, radius = 10.dp)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(book.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(book.author, style = MaterialTheme.typography.bodySmall, color = colors.inkSoft)
            Text(meta, style = MaterialTheme.typography.bodySmall, color = colors.inkMuted, modifier = Modifier.padding(top = 2.dp))
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = colors.inkMuted, modifier = Modifier.size(20.dp))
    }
}

/**
 * Плитка категории `tile()`: 120 dp, r18, цветной фон, картинка 104×112 повёрнута на 18° и вылезает за правый нижний угол.
 * Где используется: поиск (Әкиятләр · Хикәяләр · Тукай · Әмирхан).
 */
@Composable
fun CategoryTile(
    title: String,
    subtitle: String,
    color: Color,
    image: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val white = MaterialTheme.tatlibColors.onPhoto
    val imageShadow = Color.Black.copy(alpha = 0.28f)
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(TileShape)
            .background(color)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 26.dp, y = 26.dp)
                .size(104.dp, 112.dp)
                .rotate(18f)
                .shadow(8.dp, RoundedCornerShape(12.dp), ambientColor = imageShadow, spotColor = imageShadow)
                .clip(RoundedCornerShape(12.dp))
        )
        Column(
            modifier = Modifier.padding(14.dp).widthIn(max = 96.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = white)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = white.copy(alpha = 0.85f))
        }
    }
}

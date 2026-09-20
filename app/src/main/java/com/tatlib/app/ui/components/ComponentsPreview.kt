package com.tatlib.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tatlib.app.R
import com.tatlib.app.data.Book
import com.tatlib.app.data.TatarLevel
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.Ornaments
import com.tatlib.app.ui.theme.TatlibTheme
import com.tatlib.app.ui.theme.readerTypography
import com.tatlib.app.ui.theme.tatlibColors

// Данные только из gen2.py BOOKS — четыре реальные книги.
private fun book(id: String, title: String, author: String, genre: String, year: String, level: TatarLevel) =
    Book(id, title, author, genre, year, level, emptyList(), "", "", emptyList())

private val PreviewBooks = listOf(
    book("1", "Су анасы", "Габдулла Тукай", "шигъри әкият", "1908", TatarLevel.B1),
    book("2", "Шүрәле", "Габдулла Тукай", "шигъри әкият", "1907", TatarLevel.B1),
    book("3", "Нәҗип", "Фатих Әмирхан", "хикәя", "—", TatarLevel.B1),
    book("4", "Алтын әтәч", "Габдулла Тукай", "шигъри әкият", "1908", TatarLevel.B2)
)

/** Лист компонентов — повторяет Components.dc.html; чек-лист для скриншота. */
@Composable
private fun ComponentsSheet(dark: Boolean) {
    val colors = MaterialTheme.tatlibColors
    val scheme = MaterialTheme.colorScheme
    Surface(color = scheme.background) { Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Column {
            Text("TatLib · компонентлар " + (if (dark) "· караңгы тема" else "· якты тема"), style = MaterialTheme.typography.headlineLarge)
            Text("MASTER.md § 7 · v2 · 20.09.2026", style = MaterialTheme.typography.bodyMedium, color = colors.inkSoft)
        }

        Section("Шрифтлар һәм хәрефләр ә ө ү җ ң һ") {
            Text("Golos Text 600 · Рәхим итегез! Әә Өө Үү Җҗ Ңң Һһ", style = MaterialTheme.typography.headlineLarge)
            Text("Playfair Italic · Татарча күбрәк — Әә Өө Үү Җҗ Ңң Һһ", style = MaterialTheme.typography.displayMedium)
            Text("Golos Text 400 · Телне белү — дөньяны башкача күрү · Әә Өө Үү Җҗ Ңң Һһ", style = MaterialTheme.typography.bodyLarge)
            Text("Literata 18 · Нәкъ Казан артында бардыр бер авыл — «Кырлай» диләр. Әә Өө Үү Җҗ Ңң Һһ", style = MaterialTheme.readerTypography.body)
        }

        Section("Палитра") {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Swatch("bg", scheme.background); Swatch("paper", scheme.surface); Swatch("sand", scheme.surfaceVariant)
                Swatch("ink", scheme.onBackground); Swatch("forest", colors.forest); Swatch("sage", colors.sage)
                Swatch("sage2", colors.sage2); Swatch("sky2", colors.sky2); Swatch("peach2", colors.peach2)
                Swatch("cta", colors.terracotta); Swatch("error", scheme.error)
            }
        }

        Section("Кнопкалар — один компонент перехода везде") {
            ActionButton("Тест узарга", {}, ActionStyle.Primary)
            ActionButton("Үзем күрсәтермен", {}, ActionStyle.Secondary, alignEnd = true)
            Box(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(colors.forest, colors.sky)))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                ActionButton("Укуны дәвам итү", {}, ActionStyle.OnPhoto)
            }
            ReadPill("Укырга", {})
            TextLink("Китапханәгә кайту", {})
        }

        Section("Чиплар һәм дәрәҗәләр") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                FilterChip("Барысы", selected = true, onClick = {})
                FilterChip("Китаплар", selected = false, onClick = {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                TatarLevel.entries.forEach { level ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        LevelChip(level); LevelDot(level)
                    }
                }
            }
            LevelRow(selected = TatarLevel.B1, onSelect = {})
        }

        Section("Стеклянная форма на фото") {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))) {
                PhotoHero(painterResource(R.drawable.hero_blossom), 240.dp, Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent)))
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassCard {
                        GlassField("", {}, "Исем", Icons.Rounded.Person)
                        GlassField("", {}, "Серсүз", Icons.Rounded.Lock, trailingIcon = Icons.Rounded.VisibilityOff, isPassword = true)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GlassChip("14–18", selected = false, onClick = {})
                        GlassChip("18–25", selected = true, onClick = {})
                    }
                }
            }
        }

        Section("Катлам күчергече, сүз, мини-бар") {
            LayerSwitch(value = 1f, onValueChange = {})
            LayerSwitch(value = 0f, onValueChange = {}, adaptedEnabled = false)
            WordPopup("һәрьягы", TatarLevel.B1, "со всех сторон, вокруг", onClose = {}, example = "Ул авылның һәрьягы урман иде.")
            MiniReadingBar(PreviewBooks[1], onOpen = {})
        }

        Section("Прогресс, кольцо, атна, фраза") {
            ProgressLine(0.34f, Modifier.width(240.dp))
            ProgressLine(0.55f, Modifier.width(240.dp), color = colors.sky)
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp), verticalAlignment = Alignment.CenterVertically) {
                LevelRing(TatarLevel.B1, Modifier.padding(12.dp))
                WeekWaffle(setOf(0, 1, 3, 6), listOf("Дш", "Сш", "Чш", "Пҗ", "Җм", "Шм", "Як"))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                StatValue("929", "сүз"); StatValue("623", "уникаль сүз"); StatValue("—", "бүлек")
            }
            PhraseCard("Нәкъ Казан артында бардыр бер авыл — «Кырлай» диләр", "Шүрәле · Габдулла Тукай, 1907")
        }

        Section("Тышлыклар һәм узоры Figma") {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PreviewBooks.forEach { BookCover(it) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf(Ornaments.Curl, Ornaments.Tulip, Ornaments.Diamond, Ornaments.Lily).forEachIndexed { i, orn ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(orn, contentDescription = null, tint = scheme.onBackground, modifier = Modifier.size(56.dp))
                        Text("мотив ${i + 1}", style = MaterialTheme.typography.bodySmall, color = colors.inkMuted)
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        DecorStar(44.dp, colors.sky2); DecorStar(22.dp, colors.peach2)
                    }
                    Text("йолдыз", style = MaterialTheme.typography.bodySmall, color = colors.inkMuted)
                }
                DecorArc(60.dp, colors.sky2)
            }
            BookRow(PreviewBooks[1], onClick = {}, meta = "B1 · шигъри әкият · 1907")
            BookCard(PreviewBooks[0], onClick = {}, subtitle = "Габдулла Тукай · B1")
        }

        Section("Нижняя навигация") {
            Box(Modifier.width(390.dp).clip(RoundedCornerShape(12.dp)).border(1.dp, colors.line, RoundedCornerShape(12.dp))) {
                BottomNav(selectedRoute = Routes.LIBRARY, onSelect = {})
            }
        }
    } }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.tatlibColors.inkMuted)
        content()
    }
}

@Composable
private fun Swatch(name: String, color: Color) {
    Column(Modifier.width(96.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(color, RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.tatlibColors.line, RoundedCornerShape(12.dp))
        )
        Text(name, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(name = "Компонентлар · якты", widthDp = 1000, heightDp = 2600, showBackground = true)
@Composable
private fun ComponentsLightPreview() {
    TatlibTheme(darkTheme = false) { ComponentsSheet(dark = false) }
}

@Preview(name = "Компонентлар · караңгы", widthDp = 1000, heightDp = 2600, showBackground = true)
@Composable
private fun ComponentsDarkPreview() {
    TatlibTheme(darkTheme = true) { ComponentsSheet(dark = true) }
}

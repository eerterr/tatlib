package com.tatlib.app.ui.book

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.R
import com.tatlib.app.data.BookMetaTable
import com.tatlib.app.data.TatarLevel
import com.tatlib.app.data.displayYear
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.BottomSheetCard
import com.tatlib.app.ui.components.FilterChip
import com.tatlib.app.ui.components.FontSizeSlider
import com.tatlib.app.ui.components.LayerSwitch
import com.tatlib.app.ui.components.ProgressLine
import com.tatlib.app.ui.components.TextLink
import com.tatlib.app.ui.components.TopBar
import com.tatlib.app.ui.components.TopBarLeft
import com.tatlib.app.ui.components.WordPopup
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.AppPreferences
import com.tatlib.app.ui.theme.ReaderFont
import com.tatlib.app.ui.theme.ThemeMode
import com.tatlib.app.ui.theme.readerTypography
import com.tatlib.app.ui.theme.tatlibColors

private const val WORD_TAG = "WORD"

/**
 * Ридер (Reader / ReaderWord / ReaderDark.dc.html): шапка с «Аа», переключатель слоёв,
 * подпись слоя, текст страницы с тапом по слову, линия прогресса и пейджер.
 * Логика загрузки, слоёв и перевода — в AppViewModel, здесь только отображение.
 */
@Composable
fun BookReaderScreen(
    bookId: String,
    navController: NavHostController,
    viewModel: AppViewModel
) {
    val selectedBook by viewModel.selectedBook.collectAsState()
    val selectedVersion by viewModel.selectedVersion.collectAsState()
    val selectedWord by viewModel.selectedWord.collectAsState()
    val selectedWordTranslation by viewModel.selectedWordTranslation.collectAsState()
    val isLoadingTranslation by viewModel.isLoadingTranslation.collectAsState()

    LaunchedEffect(bookId) {
        bookId.toIntOrNull()?.let { id -> viewModel.loadBook(id) }
    }

    val loadError by viewModel.error.collectAsState()
    // selectedBook может хранить предыдущую книгу, пока грузится эта.
    val book = selectedBook?.takeIf { it.id == bookId }
    if (book == null) {
        ReaderMessage(loadError ?: stringResource(R.string.reader_loading)) { navController.popBackStack() }
        return
    }

    var currentPageIndex by remember(book.id) { mutableIntStateOf(0) }
    val page = book.pages.getOrNull(currentPageIndex)
    if (page == null) {
        ReaderMessage(stringResource(R.string.reader_no_text)) { navController.popBackStack() }
        return
    }

    val displayedText = when {
        selectedVersion >= 0.75f -> page.originalText
        selectedVersion >= 0.25f -> page.adaptedText.ifBlank { page.originalText }
        else -> page.russianText.ifBlank { page.originalText }
    }
    val layerName = stringResource(
        when {
            selectedVersion >= 0.75f -> R.string.layer_original
            selectedVersion >= 0.25f -> R.string.layer_adapted
            else -> R.string.layer_russian
        }
    )
    val wordCount = BookMetaTable.of(book.id)?.wordCount?.toString() ?: "—"
    val isLast = currentPageIndex >= book.pageCount - 1
    // Позиция прокрутки одна на страницу и сохраняется при смене слоя (pages/reader.md).
    val scroll = remember(book.id, currentPageIndex) { ScrollState(0) }
    val slide = with(LocalDensity.current) { 12.dp.roundToPx() }
    var showFontSheet by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            TopBar(
                left = TopBarLeft.Back,
                onLeft = { navController.popBackStack() },
                title = book.title,
                right = {
                    IconButton(onClick = { showFontSheet = true }) {
                        Icon(
                            Icons.Rounded.TextFields,
                            contentDescription = stringResource(R.string.reader_font_settings),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
            Column(
                Modifier.weight(1f).padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                LayerSwitch(value = selectedVersion, onValueChange = viewModel::selectVersion)
                Text(
                    stringResource(
                        R.string.reader_layer_caption,
                        layerName, currentPageIndex + 1, book.title, book.displayYear
                    ).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.tatlibColors.inkMuted
                )
                // «Шторка» MASTER § 4 п. 2: новый слой выезжает снизу на 12 dp с кроссфейдом 220 мс.
                AnimatedContent(
                    targetState = displayedText,
                    modifier = Modifier.weight(1f),
                    transitionSpec = {
                        val spec = tween<Float>(220, easing = FastOutSlowInEasing)
                        (fadeIn(spec) + slideInVertically(tween(220, easing = FastOutSlowInEasing)) { slide })
                            .togetherWith(fadeOut(spec))
                    },
                    label = "layer"
                ) { text ->
                    Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
                        ReaderText(
                            text = text,
                            selectedWord = selectedWord,
                            tapEnabled = selectedVersion > 0f,
                            onWordTap = { word -> viewModel.translateWord(bookId = book.id, word = word) },
                            popup = {
                                WordPopup(
                                    word = selectedWord.orEmpty(),
                                    level = null,
                                    translation = when {
                                        isLoadingTranslation -> stringResource(R.string.reader_translating)
                                        else -> selectedWordTranslation ?: stringResource(R.string.reader_translation_missing)
                                    },
                                    onClose = viewModel::clearSelectedWord,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        )
                    }
                }
            }
            Column(
                Modifier.padding(top = 14.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProgressLine(
                    (currentPageIndex + 1).toFloat() / book.pageCount,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                // Ряд с отступом 8 dp: у TextLink свои 16 dp по бокам, текст ложится на кромку 24 dp.
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (currentPageIndex > 0) {
                            TextLink(stringResource(R.string.reader_prev), onClick = {
                                currentPageIndex--
                                viewModel.clearSelectedWord()
                            })
                        }
                    }
                    // 00-ux-map п. 15: у одностраничной книги счётчик не показываем.
                    if (book.pageCount > 1) {
                        Text(
                            stringResource(R.string.reader_page_counter, currentPageIndex + 1, wordCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.tatlibColors.inkMuted
                        )
                    }
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                        if (isLast) {
                            TextLink(stringResource(R.string.reader_finish), onClick = {
                                navController.navigate(Routes.recap(book.id))
                            })
                        } else {
                            TextLink(stringResource(R.string.reader_next), onClick = {
                                currentPageIndex++
                                viewModel.clearSelectedWord()
                            })
                        }
                    }
                }
            }
        }
    }

    if (showFontSheet) {
        FontSheet(onDismiss = { showFontSheet = false })
    }
}

/** Состояния «Китап йөкләнә…» / «Текст табылмады»: шапка с «назад» и одна строка bodyLarge inkSoft. */
@Composable
private fun ReaderMessage(text: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopBar(left = TopBarLeft.Back, onLeft = onBack)
            Text(
                text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.tatlibColors.inkSoft,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
    }
}

/** Слово, по которому тапнули: границы в тексте и логический конец его строки (начало следующей). */
private data class WordSelection(val start: Int, val end: Int, val lineEnd: Int)

/**
 * Текст страницы с тапом по слову. Всплывашка встаёт сразу под строкой слова: текст режется по
 * `TextLayoutResult.getLineEnd(getLineForOffset(offset))` на «до» и «после», между ними — `popup`.
 * `hardWords` — слова выше уровня читателя (нормализованное слово → уровень): подчёркивание 1.5 dp
 * цветом точки уровня рисуется в `drawBehind` по `getBoundingBox`; без API токенов карта пуста.
 */
@Composable
private fun ReaderText(
    text: String,
    selectedWord: String?,
    tapEnabled: Boolean,
    onWordTap: (String) -> Unit,
    popup: @Composable () -> Unit,
    hardWords: Map<String, TatarLevel> = emptyMap()
) {
    var selection by remember(text) { mutableStateOf<WordSelection?>(null) }
    val active = if (selectedWord != null) selection else null
    val highlight = MaterialTheme.tatlibColors.level(TatarLevel.B1).container
    val annotated = remember(text, active, highlight) { buildWordAnnotatedString(text, active, highlight) }
    val underline = if (AppPreferences.underlineHardWords) hardWords else emptyMap()
    val onTap: (String, Int, Int, Int) -> Unit = { word, start, end, lineEnd ->
        selection = WordSelection(start, end, lineEnd)
        onWordTap(word)
    }
    val split = active?.lineEnd
    if (split == null || split >= annotated.length) {
        ReaderPiece(annotated, 0, underline, tapEnabled, onTap)
        if (split != null) popup()
    } else {
        ReaderPiece(annotated.subSequence(0, split), 0, underline, tapEnabled, onTap)
        popup()
        ReaderPiece(annotated.subSequence(split, annotated.length), split, underline, tapEnabled, onTap)
    }
}

@Composable
private fun ReaderPiece(
    annotated: AnnotatedString,
    baseOffset: Int,
    hardWords: Map<String, TatarLevel>,
    tapEnabled: Boolean,
    onTap: (word: String, start: Int, end: Int, lineEnd: Int) -> Unit
) {
    if (annotated.isEmpty()) return
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val colors = MaterialTheme.tatlibColors
    val thickness = with(LocalDensity.current) { 1.5.dp.toPx() }
    val underlines = remember(annotated, hardWords) {
        if (hardWords.isEmpty()) emptyList()
        else annotated.getStringAnnotations(WORD_TAG, 0, annotated.length).mapNotNull { range ->
            hardWords[normalizeWord(range.item)]?.let { level -> Triple(range.start, range.end, level) }
        }
    }
    ClickableText(
        text = annotated,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val result = layout ?: return@drawBehind
                underlines.forEach { (start, end, level) ->
                    val line = result.getLineForOffset(start)
                    // ponytail: слово с переносом на другую строку не подчёркиваем — в стихах таких нет
                    if (end <= start || result.getLineForOffset(end - 1) != line) return@forEach
                    val first = result.getBoundingBox(start)
                    val last = result.getBoundingBox(end - 1)
                    val y = last.bottom - thickness / 2
                    drawLine(colors.level(level).dot, Offset(first.left, y), Offset(last.right, y), thickness)
                }
            },
        style = MaterialTheme.readerTypography.body.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start
        ),
        onTextLayout = { layout = it },
        onClick = { offset ->
            if (!tapEnabled) return@ClickableText
            val result = layout ?: return@ClickableText
            annotated.getStringAnnotations(WORD_TAG, offset, offset).firstOrNull()?.let { range ->
                val lineEnd = result.getLineEnd(result.getLineForOffset(offset))
                onTap(range.item, baseOffset + range.start, baseOffset + range.end, baseOffset + lineEnd)
            }
        }
    )
}

private fun normalizeWord(word: String): String = word.trim { !it.isLetterOrDigit() }.lowercase()

/**
 * Разбивает текст регуляркой `\S+|\s+`: пробелы и переносы строк сохраняются как есть,
 * каждое слово получает аннотацию WORD; выбранное слово — фон `.hl` (level B1 container).
 */
private fun buildWordAnnotatedString(
    text: String,
    highlight: WordSelection?,
    highlightColor: Color
): AnnotatedString = buildAnnotatedString {
    Regex("""\S+|\s+""").findAll(text).forEach { match ->
        val part = match.value
        if (part.all { it.isWhitespace() }) {
            append(part)
        } else {
            pushStringAnnotation(tag = WORD_TAG, annotation = part.trim())
            if (highlight != null && match.range.first == highlight.start) {
                withStyle(SpanStyle(background = highlightColor)) { append(part) }
            } else {
                append(part)
            }
            pop()
        }
    }
}

/** Лист «Аа»: шрифт ридера, кегль, тема — пишет в AppPreferences (подписи из Profile.dc.html). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FontSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f),
        dragHandle = null
    ) {
        BottomSheetCard {
            SettingRow(stringResource(R.string.reader_font_label)) {
                FilterChip(
                    stringResource(R.string.reader_font_literata),
                    selected = AppPreferences.readerFont == ReaderFont.LITERATA,
                    onClick = { AppPreferences.readerFont = ReaderFont.LITERATA }
                )
                FilterChip(
                    stringResource(R.string.reader_font_golos),
                    selected = AppPreferences.readerFont == ReaderFont.GOLOS,
                    onClick = { AppPreferences.readerFont = ReaderFont.GOLOS }
                )
            }
            Column(Modifier.padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(R.string.reader_font_size_label),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                FontSizeSlider(
                    valueSp = AppPreferences.readerFontSizeSp,
                    onChange = { AppPreferences.readerFontSizeSp = it }
                )
                Text(
                    stringResource(R.string.reader_font_size_hint, AppPreferences.readerFontSizeSp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.tatlibColors.inkMuted
                )
            }
            SettingRow(stringResource(R.string.reader_theme_label)) {
                FilterChip(
                    stringResource(R.string.reader_theme_system),
                    selected = AppPreferences.themeMode == ThemeMode.SYSTEM,
                    onClick = { AppPreferences.themeMode = ThemeMode.SYSTEM }
                )
                FilterChip(
                    stringResource(R.string.reader_theme_light),
                    selected = AppPreferences.themeMode == ThemeMode.LIGHT,
                    onClick = { AppPreferences.themeMode = ThemeMode.LIGHT }
                )
                FilterChip(
                    stringResource(R.string.reader_theme_dark),
                    selected = AppPreferences.themeMode == ThemeMode.DARK,
                    onClick = { AppPreferences.themeMode = ThemeMode.DARK }
                )
            }
        }
    }
}

/** Строка настройки `srow()`: подпись bodyLarge слева, чипы справа, min 52 dp. */
@Composable
private fun SettingRow(label: String, chips: @Composable () -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 52.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { chips() }
    }
}

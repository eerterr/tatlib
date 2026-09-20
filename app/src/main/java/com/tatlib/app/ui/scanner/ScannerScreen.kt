package com.tatlib.app.ui.scanner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.AppContextHolder
import com.tatlib.app.R
import com.tatlib.app.data.ApiClient
import com.tatlib.app.data.Book
import com.tatlib.app.data.BookMetaTable
import com.tatlib.app.data.OcrTranslateRequest
import com.tatlib.app.data.TatarLevel
import com.tatlib.app.ui.components.BookCover
import com.tatlib.app.ui.components.BottomSheetCard
import com.tatlib.app.ui.components.LayerSwitch
import com.tatlib.app.ui.components.LevelChip
import com.tatlib.app.ui.components.ReadPill
import com.tatlib.app.ui.components.RoundButton
import com.tatlib.app.ui.components.TextLink
import com.tatlib.app.ui.components.TopBar
import com.tatlib.app.ui.components.TopBarLeft
import com.tatlib.app.ui.components.Viewfinder
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.Ornaments
import com.tatlib.app.ui.theme.readerTypography
import com.tatlib.app.ui.theme.tatlibColors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Сканер (ScannerStart / Busy / Text / Match.dc.html): системная камера или галерея → OCR →
 * текст с переключателем Оригинал / Русча → карточка и лист «Китап табылды», если бэкенд узнал книгу.
 * Нижняя навигация — от TatlibApp.
 */
@Composable
fun ScannerScreen(
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var recognizedText by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }
    var bookFound by remember { mutableStateOf(false) }
    var bookId by remember { mutableStateOf<Int?>(null) }
    var bookTitle by remember { mutableStateOf<String?>(null) }
    var bookAuthor by remember { mutableStateOf<String?>(null) }
    // 1 = Оригинал (татарский), 0 = Русча; «Адаптация» для OCR-текста недоступна.
    var layer by remember { mutableFloatStateOf(1f) }
    var showSheet by remember { mutableStateOf(false) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var job by remember { mutableStateOf<Job?>(null) }
    // OCR вернул пустой текст без книги — иначе экран молча возвращается к старту.
    val noTextMessage = stringResource(R.string.reader_no_text)

    fun reset() {
        job?.cancel()
        job = null
        isLoading = false
        error = null
        recognizedText = ""
        translatedText = ""
        bookFound = false
        bookId = null
        bookTitle = null
        bookAuthor = null
        layer = 1f
        showSheet = false
    }

    fun processImage(uri: Uri) {
        reset()
        job = scope.launch {
            isLoading = true
            try {
                val file = withContext(Dispatchers.IO) { copyUriToCache(uri) }
                val requestFile = file.asRequestBody("image/jpeg".toMediaType())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = ApiClient.apiService.recognizeImage(body)

                recognizedText = response.text
                bookFound = response.book_found
                bookId = response.book_id
                bookTitle = response.book_title
                bookAuthor = response.book_author
                if (response.text.isBlank() && !response.book_found) error = noTextMessage

                // Перевод запрашиваем при любом непустом тексте, чтобы слой «Русча» работал и для найденной книги.
                if (response.text.isNotBlank()) {
                    val translationResponse = ApiClient.apiService.translateOcrText(
                        OcrTranslateRequest(text = response.text)
                    )
                    translatedText = translationResponse.translation
                }
            } catch (e: CancellationException) {
                throw e // «Туктату»: отмена — не ошибка
            } catch (e: Exception) {
                error = e.message ?: "Не удалось распознать изображение"
            } finally {
                isLoading = false
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) cameraUri?.let { processImage(it) }
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { processImage(it) }
    }
    val openGallery = { galleryLauncher.launch("image/*") }
    val openCamera = {
        val uri = AppContextHolder.createImageUri()
        cameraUri = uri
        cameraLauncher.launch(uri)
    }

    // Делегированные state-переменные не смарткастятся — id читаем в локальную val.
    val found = bookId
    val foundBook = if (bookFound && found != null) scannedBook(found, bookTitle, bookAuthor) else null

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            when {
                isLoading -> ScannerBusy(onClose = { navController.popBackStack() }, onGallery = openGallery, onStop = ::reset)
                recognizedText.isNotBlank() -> ScannerText(
                    recognizedText = recognizedText,
                    translatedText = translatedText,
                    layer = layer,
                    onLayerChange = { layer = it },
                    foundBook = foundBook,
                    onBack = ::reset,
                    onOpenSheet = { showSheet = true }
                )
                else -> ScannerStart(
                    error = error,
                    onClose = { navController.popBackStack() },
                    onGallery = openGallery,
                    onCamera = openCamera
                )
            }
        }
    }

    if (showSheet && foundBook != null) {
        MatchSheet(
            book = foundBook,
            onRead = {
                showSheet = false
                navController.navigate(Routes.bookDetail(foundBook.id))
            },
            onDismiss = { showSheet = false }
        )
    }
}

/** Книга только для обложки и подписей: OCR отдаёт id, название и автора, остальное — из BookMetaTable. */
private fun scannedBook(id: Int, title: String?, author: String?) = Book(
    id = id.toString(),
    title = title.orEmpty(),
    author = author.orEmpty(),
    genre = "",
    year = "",
    level = TatarLevel.B1,
    tags = emptyList(),
    description = "",
    readingTimeLabel = "",
    pages = emptyList()
)

@Composable
private fun ScanTop(onClose: () -> Unit, onGallery: () -> Unit) {
    TopBar(
        left = TopBarLeft.Close,
        onLeft = onClose,
        right = {
            IconButton(onClick = onGallery) {
                Icon(
                    Icons.Rounded.Image,
                    contentDescription = stringResource(R.string.scanner_gallery),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    )
}

/** Состояние 1: старт. Превью камеры в приложении нет (системная TakePicture) — визир пустой, sand. */
@Composable
private fun ScannerStart(error: String?, onClose: () -> Unit, onGallery: () -> Unit, onCamera: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val colors = MaterialTheme.tatlibColors
    ScanTop(onClose, onGallery)
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            stringResource(R.string.scanner_start_title),
            style = MaterialTheme.typography.headlineMedium,
            color = scheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(error, style = MaterialTheme.typography.bodyMedium, color = scheme.error, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        Viewfinder(painter = null) {
            Icon(
                Icons.Rounded.CameraAlt,
                contentDescription = null,
                tint = colors.onPhoto.copy(alpha = 0.7f),
                modifier = Modifier.size(40.dp)
            )
        }
        Text(
            stringResource(R.string.scanner_start_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.inkSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(18.dp)) // колонка прокручивается: weight тут не работает
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextLink(stringResource(R.string.scanner_gallery), onClick = onGallery)
            RoundButton(
                onClick = onCamera,
                contentDescription = stringResource(R.string.scanner_camera),
                color = scheme.primary,
                contentColor = scheme.onPrimary,
                size = 72.dp,
                icon = Icons.Rounded.CameraAlt,
                iconSize = 28.dp,
                modifier = Modifier.border(4.dp, colors.sage2, CircleShape)
            )
            Spacer(Modifier.width(76.dp))
        }
    }
}

/** Состояние 2: распознавание. Реального прогресса у OCR нет — индикатор неопределённый. */
@Composable
private fun ScannerBusy(onClose: () -> Unit, onGallery: () -> Unit, onStop: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val colors = MaterialTheme.tatlibColors
    ScanTop(onClose, onGallery)
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            stringResource(R.string.scanner_busy_title),
            style = MaterialTheme.typography.headlineMedium,
            color = scheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Viewfinder(painter = null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CircularProgressIndicator(
                    color = colors.onPhoto,
                    trackColor = colors.onPhoto.copy(alpha = 0.4f),
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    stringResource(R.string.scanner_busy_engine),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onPhoto
                )
            }
        }
        LinearProgressIndicator(
            color = colors.sky,
            trackColor = scheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
        )
        Text(
            stringResource(R.string.scanner_busy_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.inkSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(18.dp)) // колонка прокручивается: weight тут не работает
        TextLink(stringResource(R.string.scanner_stop), onClick = onStop, fullWidth = true)
    }
}

/** Состояние 3: распознанный текст с переключателем Оригинал / Русча и карточкой найденной книги. */
@Composable
private fun ScannerText(
    recognizedText: String,
    translatedText: String,
    layer: Float,
    onLayerChange: (Float) -> Unit,
    foundBook: Book?,
    onBack: () -> Unit,
    onOpenSheet: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val colors = MaterialTheme.tatlibColors
    val isOriginal = layer >= 0.5f
    TopBar(
        left = TopBarLeft.Back,
        onLeft = onBack,
        title = stringResource(R.string.scanner_text_title),
        right = {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Rounded.TextFields,
                    contentDescription = stringResource(R.string.reader_font_settings),
                    tint = scheme.onBackground
                )
            }
        }
    )
    Column(
        Modifier.fillMaxSize().padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.scanner_complexity).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.inkMuted
            )
            LayerSwitch(value = layer, onValueChange = onLayerChange, adaptedEnabled = false)
        }
        Text(
            stringResource(
                R.string.scanner_layer_caption,
                stringResource(if (isOriginal) R.string.layer_original else R.string.layer_russian)
            ).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = colors.inkMuted
        )
        Box(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())) {
            Text(
                if (isOriginal) recognizedText else translatedText.ifBlank { stringResource(R.string.reader_translation_missing) },
                style = MaterialTheme.readerTypography.body,
                color = scheme.onSurface
            )
        }
        if (foundBook != null) {
            val shape = MaterialTheme.shapes.medium
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(colors.sageContainer)
                    .border(1.dp, colors.line, shape)
                    .clickable(onClick = onOpenSheet)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BookCover(foundBook, width = 44.dp, height = 62.dp, radius = 8.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        stringResource(R.string.scanner_book_found).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.forest
                    )
                    Text(
                        stringResource(R.string.scanner_book_title_author, foundBook.title, foundBook.author),
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.onSurface
                    )
                    Text(
                        stringResource(R.string.scanner_book_in_base),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.inkMuted
                    )
                }
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = colors.inkMuted, modifier = Modifier.size(20.dp))
            }
        }
    }
}

/** Состояние 4: лист «Китап табылды» (ScannerMatch.dc.html) поверх затемнения 45 %. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchSheet(book: Book, onRead: () -> Unit, onDismiss: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val colors = MaterialTheme.tatlibColors
    val meta = BookMetaTable.of(book.id)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        scrimColor = scheme.scrim.copy(alpha = 0.45f),
        dragHandle = null
    ) {
        BottomSheetCard {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Ornaments.Tulip, contentDescription = null, tint = colors.forest, modifier = Modifier.size(22.dp))
                    Text(
                        stringResource(R.string.scanner_book_found).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.forest
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    BookCover(book, width = 88.dp, height = 124.dp, radius = 12.dp)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(book.title, style = MaterialTheme.typography.headlineMedium, color = scheme.onSurface)
                        Text(
                            stringResource(R.string.scanner_sheet_author_year, book.author, meta?.year?.toString() ?: "—"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.inkSoft
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            LevelChip(meta?.level ?: TatarLevel.B1)
                            Text(
                                stringResource(
                                    R.string.scanner_sheet_stats,
                                    meta?.wordCount?.toString() ?: "—",
                                    meta?.blocks?.toString() ?: "—"
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.inkMuted
                            )
                        }
                    }
                }
                Text(
                    stringResource(R.string.scanner_sheet_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.inkSoft
                )
                ReadPill(stringResource(R.string.scanner_sheet_read), onClick = onRead, fullWidth = true)
                TextLink(stringResource(R.string.scanner_sheet_dismiss), onClick = onDismiss, fullWidth = true)
            }
        }
    }
}

private fun copyUriToCache(uri: Uri): File {
    val context = AppContextHolder.context

    val file = File.createTempFile("tatlib_ocr_", ".jpg", context.cacheDir)

    context.contentResolver.openInputStream(uri).use { input ->
        requireNotNull(input) { "Не удалось открыть изображение" }
        file.outputStream().use { output -> input.copyTo(output) }
    }

    return file
}

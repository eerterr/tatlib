package com.tatlib.app.ui.scanner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.AppContextHolder
import com.tatlib.app.data.ApiClient
import com.tatlib.app.data.OcrTranslateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Composable
fun ScannerScreen(
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()

    var isLoading by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var recognizedText by remember {
        mutableStateOf("")
    }

    var translatedText by remember {
        mutableStateOf("")
    }

    var bookFound by remember {
        mutableStateOf(false)
    }

    var bookId by remember {
        mutableStateOf<Int?>(null)
    }

    var bookTitle by remember {
        mutableStateOf<String?>(null)
    }

    // 0 = татарский оригинал
    // 1 = русский перевод
    var translationSlider by remember {
        mutableStateOf(0f)
    }

    var cameraUri by remember {
        mutableStateOf<Uri?>(null)
    }

    fun processImage(uri: Uri) {
        scope.launch {
            isLoading = true
            error = null
            recognizedText = ""
            translatedText = ""
            bookFound = false
            bookId = null
            bookTitle = null
            translationSlider = 0f

            try {
                val file = withContext(Dispatchers.IO) {
                    copyUriToCache(uri)
                }

                val requestFile = file.asRequestBody(
                    "image/jpeg".toMediaType()
                )

                val body = MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    requestFile
                )

                val response = ApiClient.apiService
                    .recognizeImage(body)

                recognizedText = response.text
                bookFound = response.book_found
                bookId = response.book_id
                bookTitle = response.book_title

                if (!response.book_found && response.text.isNotBlank()) {
                    val translationResponse =
                        ApiClient.apiService.translateOcrText(
                            OcrTranslateRequest(
                                text = response.text
                            )
                        )

                    translatedText = translationResponse.translation
                }

            } catch (e: Exception) {
                error = e.message ?: "Не удалось распознать изображение"
            } finally {
                isLoading = false
            }
        }
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->

            if (success) {
                cameraUri?.let {
                    processImage(it)
                }
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            uri?.let {
                processImage(it)
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {

        Text(
            text = "Сканер",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(
                top = 24.dp,
                bottom = 20.dp
            )
        )

        Text(
            text = "Сфотографируйте страницу татарского текста или выберите изображение из галереи.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = {
                    val uri = AppContextHolder.createImageUri()
                    cameraUri = uri
                    cameraLauncher.launch(uri)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("📷 Камера")
            }

            OutlinedButton(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("🖼 Галерея")
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (isLoading) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator()

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Распознаём текст..."
                )
            }
        }

        error?.let { message ->

            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 20.dp)
            )
        }

        if (bookFound && bookId != null) {

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Книга найдена",
                style = MaterialTheme.typography.titleLarge
            )

            bookTitle?.let { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = {
                    navController.navigate(
                        "book_reader/${bookId}"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Открыть книгу")
            }
        }

        if (recognizedText.isNotBlank() && !bookFound) {

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "Распознанный текст",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Slider(
                value = translationSlider,
                onValueChange = {
                    translationSlider = if (it < 0.5f) 0f else 1f
                },
                steps = 0,
                valueRange = 0f..1f
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Татарский")
                Text("Русский")
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text =
                    if (translationSlider < 0.5f) {
                        recognizedText
                    } else {
                        translatedText.ifBlank {
                            "Перевод не найден"
                        }
                    },
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(
            modifier = Modifier.height(40.dp)
        )
    }
}

private fun copyUriToCache(uri: Uri): File {
    val context = AppContextHolder.context

    val file = File.createTempFile(
        "tatlib_ocr_",
        ".jpg",
        context.cacheDir
    )

    context.contentResolver.openInputStream(uri).use { input ->
        requireNotNull(input) {
            "Не удалось открыть изображение"
        }

        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return file
}
package com.tatlib.app.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/**
 * Настройки внешнего вида, которые меняет экран профиля (тема, шрифт и кегль ридера,
 * подчёркивание слов выше уровня). Живут в памяти процесса: `AppViewModel.kt` не трогаем
 * по условию фазы, хранилища настроек в проекте нет.
 */
// ponytail: без DataStore — сброс при перезапуске; добавить persistence, когда появится слой настроек
object AppPreferences {
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
    var readerFont by mutableStateOf(ReaderFont.LITERATA)
    var readerFontSizeSp by mutableIntStateOf(18)
    var underlineHardWords by mutableStateOf(true)
}

package com.tatlib.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.R
import com.tatlib.app.data.UserMeta
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.AppLanguage
import com.tatlib.app.ui.components.FilterChip
import com.tatlib.app.ui.components.FontSizeSlider
import com.tatlib.app.ui.components.LanguageToggle
import com.tatlib.app.ui.components.LevelChip
import com.tatlib.app.ui.components.PhotoHero
import com.tatlib.app.ui.components.TextLink
import com.tatlib.app.ui.components.Toggle
import com.tatlib.app.ui.components.TopBar
import com.tatlib.app.ui.components.TopBarLeft
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.AppPreferences
import com.tatlib.app.ui.theme.Ornaments
import com.tatlib.app.ui.theme.ReaderFont
import com.tatlib.app.ui.theme.ThemeMode
import com.tatlib.app.ui.theme.tatlibColors

@Composable
fun ProfileScreen(navController: NavHostController, viewModel: AppViewModel) {
    val colors = MaterialTheme.tatlibColors
    val scheme = MaterialTheme.colorScheme
    var language by remember { mutableStateOf(AppLanguage.TAT) }

    Box(Modifier.fillMaxSize().background(scheme.background)) {
        // photo_hero(): 220 dp, «center 30%» → вертикальный bias 0.3 · 2 − 1 = −0.4; scrim .1 → background 100 %.
        PhotoHero(
            painter = painterResource(R.drawable.arch_mosque),
            height = 220.dp,
            overlay = Brush.verticalGradient(0f to scheme.scrim.copy(alpha = 0.1f), 1f to scheme.background),
            contentAlignment = BiasAlignment(horizontalBias = 0f, verticalBias = -0.4f)
        )
        // Без прокрутки, как в макете: «Чыгу» прижат к низу через weight (в scroll-контейнере weight не работает).
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopBar(
                left = TopBarLeft.Back,
                onLeft = { navController.popBackStack() },
                onPhoto = true,
                right = {
                    // В макете без действия.
                    IconButton(onClick = {}, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Rounded.Settings, contentDescription = null, tint = colors.onPhoto)
                    }
                }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 24.dp, end = 24.dp, top = 60.dp, bottom = 24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(6.dp, CircleShape, ambientColor = colors.shadow, spotColor = colors.shadow)
                            .background(scheme.surface, CircleShape)
                            .border(3.dp, colors.onPhoto, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Ornaments.Lily, contentDescription = null, tint = colors.forest, modifier = Modifier.size(34.dp))
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stringResource(R.string.profile_name), style = MaterialTheme.typography.headlineMedium, color = scheme.onBackground)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            LevelChip(UserMeta.level)
                            Text(
                                stringResource(R.string.profile_age, UserMeta.age),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.inkMuted
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(scheme.surface, MaterialTheme.shapes.medium)
                        .border(1.dp, colors.line, MaterialTheme.shapes.medium)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    SettingRow(stringResource(R.string.profile_language), stringResource(R.string.profile_language_sub)) {
                        LanguageToggle(
                            language = language,
                            onToggle = { language = if (language == AppLanguage.TAT) AppLanguage.RU else AppLanguage.TAT }
                        )
                    }
                    SettingRow(stringResource(R.string.profile_theme)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ThemeMode.entries.forEach { mode ->
                                FilterChip(
                                    text = stringResource(
                                        when (mode) {
                                            ThemeMode.SYSTEM -> R.string.profile_theme_system
                                            ThemeMode.LIGHT -> R.string.profile_theme_light
                                            ThemeMode.DARK -> R.string.profile_theme_dark
                                        }
                                    ),
                                    selected = AppPreferences.themeMode == mode,
                                    onClick = { AppPreferences.themeMode = mode }
                                )
                            }
                        }
                    }
                    SettingRow(stringResource(R.string.profile_reader_font)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                text = stringResource(R.string.profile_font_literata),
                                selected = AppPreferences.readerFont == ReaderFont.LITERATA,
                                onClick = { AppPreferences.readerFont = ReaderFont.LITERATA }
                            )
                            FilterChip(
                                text = stringResource(R.string.profile_font_golos),
                                selected = AppPreferences.readerFont == ReaderFont.GOLOS,
                                onClick = { AppPreferences.readerFont = ReaderFont.GOLOS }
                            )
                        }
                    }
                    Column(modifier = Modifier.padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.profile_font_size), style = MaterialTheme.typography.bodyLarge, color = scheme.onSurface)
                        FontSizeSlider(
                            valueSp = AppPreferences.readerFontSizeSp,
                            onChange = { AppPreferences.readerFontSizeSp = it }
                        )
                        Text(
                            stringResource(R.string.profile_font_size_value, AppPreferences.readerFontSizeSp),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.inkMuted
                        )
                    }
                    SettingRow(stringResource(R.string.profile_underline), stringResource(R.string.profile_underline_sub)) {
                        Toggle(
                            checked = AppPreferences.underlineHardWords,
                            onChange = { AppPreferences.underlineHardWords = it }
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                TextLink(
                    text = stringResource(R.string.profile_logout),
                    icon = Icons.AutoMirrored.Rounded.Logout,
                    fullWidth = true,
                    onClick = {
                        navController.navigate(Routes.ONBOARDING_WELCOME) { popUpTo(0) }
                    }
                )
            }
        }
    }
}

/** Строка настроек `srow()`: подпись bodyLarge (+ bodySmall inkMuted), контрол справа, min 52 dp, gap 12. */
@Composable
private fun SettingRow(label: String, sub: String? = null, control: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            if (sub != null) {
                Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.tatlibColors.inkMuted)
            }
        }
        control()
    }
}

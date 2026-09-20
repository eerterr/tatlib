package com.tatlib.app.ui.leveltest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.R
import com.tatlib.app.data.TatarLevel
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.ActionButton
import com.tatlib.app.ui.components.ActionStyle
import com.tatlib.app.ui.components.AppLanguage
import com.tatlib.app.ui.components.ArchPhoto
import com.tatlib.app.ui.components.LanguageToggle
import com.tatlib.app.ui.components.LevelRow
import com.tatlib.app.ui.components.TopBar
import com.tatlib.app.ui.components.TopBarLeft
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.tatlibColors

@Composable
fun LevelResultScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    var selectedLevel by remember { mutableStateOf(TatarLevel.B1) }
    var language by remember { mutableStateOf(AppLanguage.TAT) }

    fun goToLibrary(level: TatarLevel) {
        selectedLevel = level
        navController.navigate(Routes.LIBRARY) {
            popUpTo(Routes.ONBOARDING_WELCOME) { inclusive = true }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clipToBounds()
    ) {
        Box(Modifier.fillMaxSize().statusBarsPadding()) {
            DecoA()
            // arch(): right −30, top 470, 200×300 → TopEnd + offset(x = 30).
            ArchPhoto(
                painterResource(R.drawable.kremlin_bottom),
                Modifier.align(Alignment.TopEnd).offset(x = 30.dp, y = 470.dp).size(200.dp, 300.dp)
            )
            Column(Modifier.fillMaxSize()) {
                TopBar(
                    left = TopBarLeft.Back,
                    onLeft = { navController.popBackStack() },
                    right = {
                        LanguageToggle(
                            language = language,
                            onToggle = { language = if (language == AppLanguage.TAT) AppLanguage.RU else AppLanguage.TAT }
                        )
                    }
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 24.dp, end = 24.dp, top = 110.dp, bottom = 24.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    Text(
                        stringResource(R.string.level_result_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    LevelRow(selected = selectedLevel, onSelect = { selectedLevel = it })
                    Text(
                        stringResource(R.string.level_result_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.tatlibColors.inkSoft,
                        modifier = Modifier.widthIn(max = 250.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    ActionButton(
                        text = stringResource(R.string.level_result_continue),
                        onClick = { goToLibrary(selectedLevel) },
                        style = ActionStyle.Primary
                    )
                    ActionButton(
                        text = stringResource(R.string.level_result_unknown),
                        onClick = { goToLibrary(TatarLevel.A1) },
                        style = ActionStyle.Secondary,
                        alignEnd = true,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

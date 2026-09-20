package com.tatlib.app.ui.leveltest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import com.tatlib.app.ui.components.ActionButton
import com.tatlib.app.ui.components.ActionStyle
import com.tatlib.app.ui.components.AppLanguage
import com.tatlib.app.ui.components.ArchPhoto
import com.tatlib.app.ui.components.DecorArc
import com.tatlib.app.ui.components.DecorStar
import com.tatlib.app.ui.components.LanguageToggle
import com.tatlib.app.ui.components.TopBar
import com.tatlib.app.ui.components.TopBarLeft
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.tatlibColors

/** Декор `DECO_A` из gen2.py: две дуги и пять звёзд; позиции — px макета 390×844 как dp. */
@Composable
internal fun BoxScope.DecoA() {
    val colors = MaterialTheme.tatlibColors
    DecorArc(520.dp, colors.sky2, Modifier.offset(x = 250.dp, y = (-420).dp))
    DecorArc(700.dp, colors.peach2, Modifier.offset(x = (-330).dp, y = 470.dp))
    DecorStar(40.dp, colors.sky2, Modifier.offset(x = 318.dp, y = 150.dp))
    DecorStar(16.dp, colors.peach2, Modifier.offset(x = 262.dp, y = 330.dp))
    DecorStar(60.dp, colors.sky2, Modifier.offset(x = 20.dp, y = 545.dp))
    DecorStar(20.dp, colors.peach2, Modifier.offset(x = 80.dp, y = 620.dp))
    DecorStar(22.dp, colors.peach2, Modifier.offset(x = 250.dp, y = 555.dp))
}

@Composable
fun LevelIntroScreen(navController: NavHostController) {
    var language by remember { mutableStateOf(AppLanguage.TAT) }
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clipToBounds()
    ) {
        // Декор и фото сдвигаются вместе с контентом под статус-бар — геометрия макета сохраняется.
        Box(Modifier.fillMaxSize().statusBarsPadding()) {
            DecoA()
            // arch(): right −20, top 440, 210×330 → TopEnd + offset(x = 20).
            ArchPhoto(
                painterResource(R.drawable.arch_mosque),
                Modifier.align(Alignment.TopEnd).offset(x = 20.dp, y = 440.dp).size(210.dp, 330.dp)
            )
            Column(Modifier.fillMaxSize()) {
                TopBar(
                    left = TopBarLeft.Logo,
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
                        .padding(start = 24.dp, end = 24.dp, top = 150.dp, bottom = 24.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(R.string.level_intro_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.widthIn(max = 300.dp)
                    )
                    Text(
                        stringResource(R.string.level_intro_text),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.tatlibColors.inkSoft,
                        modifier = Modifier.widthIn(max = 250.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    ActionButton(
                        text = stringResource(R.string.level_intro_take_test),
                        onClick = { navController.navigate(Routes.LEVEL_QUIZ) },
                        style = ActionStyle.Primary
                    )
                    ActionButton(
                        text = stringResource(R.string.level_intro_self),
                        onClick = { navController.navigate(Routes.levelResult()) },
                        style = ActionStyle.Secondary,
                        alignEnd = true,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

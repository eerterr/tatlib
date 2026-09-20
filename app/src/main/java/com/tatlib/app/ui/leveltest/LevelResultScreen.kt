package com.tatlib.app.ui.leveltest

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.data.TatarLevel
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.ScreenTopBar
import com.tatlib.app.ui.components.WideButton
import com.tatlib.app.ui.components.WideOutlinedButton
import com.tatlib.app.ui.components.colorForLevel
import com.tatlib.app.ui.navigation.Routes

@Composable
fun LevelResultScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    var selectedLevel by remember {
        mutableStateOf(TatarLevel.B1)
    }

    fun goToLibrary(level: TatarLevel) {
        selectedLevel = level

        navController.navigate(Routes.LIBRARY) {
            popUpTo(Routes.ONBOARDING_WELCOME) {
                inclusive = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        ScreenTopBar(
            onBack = {
                navController.popBackStack()
            },
            showLanguageToggle = false,
            modifier = Modifier.padding(top = 16.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Сезнең Татар теле дәрәҗәсе",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 36.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TatarLevel.entries.forEach { level ->

                    val selected = level == selectedLevel
                    val circleSize =
                        if (selected) 56.dp else 40.dp

                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .background(
                                color =
                                    if (selected) {
                                        colorForLevel(level)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                shape = CircleShape
                            )
                            .clickable {
                                selectedLevel = level
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = level.label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color =
                                if (selected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                        )
                    }
                }
            }

            Text(
                text = "Дәрәҗәгезне төзәтергә телисез икән, теләгән хәрефкә басыгыз.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 20.dp)
            )
        }

        WideButton(
            text = "Дәвам итү",
            onClick = {
                goToLibrary(selectedLevel)
            },
            modifier = Modifier.fillMaxWidth()
        )

        WideOutlinedButton(
            text = "Татар телен белмим",
            onClick = {
                goToLibrary(TatarLevel.A1)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 12.dp,
                    bottom = 32.dp
                )
        )
    }
}
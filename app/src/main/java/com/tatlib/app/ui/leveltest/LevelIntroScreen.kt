package com.tatlib.app.ui.leveltest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.ui.components.KazanSunsetHero
import com.tatlib.app.ui.components.RoundBackButton
import com.tatlib.app.ui.components.WideButton
import com.tatlib.app.ui.components.WideOutlinedButton
import com.tatlib.app.ui.navigation.Routes

@Composable
fun LevelIntroScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        KazanSunsetHero(modifier = Modifier.fillMaxWidth().height(300.dp))

        Column(modifier = Modifier.fillMaxSize()) {
            RoundBackButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(start = 14.dp, top = 12.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    "Сезнең дәрәҗәгезне билгелибезме?",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Татар телен ни дәрәҗәдә белүегезне ачыклагыз.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp, bottom = 32.dp)
                )

                WideButton(
                    text = "Тест узарга",
                    onClick = { navController.navigate(Routes.LEVEL_QUIZ) },
                    modifier = Modifier.fillMaxWidth()
                )
                WideOutlinedButton(
                    text = "Үзем күрсәтермен",
                    onClick = { navController.navigate(Routes.LEVEL_RESULT) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 40.dp)
                )
            }
        }
    }
}

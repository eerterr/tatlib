package com.tatlib.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.ui.components.ScreenTopBar
import com.tatlib.app.ui.components.WideButton
import com.tatlib.app.ui.components.WideOutlinedButton
import com.tatlib.app.ui.navigation.Routes

@Composable
fun AuthChoiceScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        ScreenTopBar(
            onBack = { navController.popBackStack() },
            showLanguageToggle = false,
            modifier = Modifier.padding(top = 16.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Сездә исәп язмасы бармы?",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Дәвам итү өчен теркәлегез яки хәзерге хисабыгызга керегез.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp, bottom = 40.dp)
            )

            WideButton(
                text = "Теркәлү",
                onClick = { navController.navigate(Routes.REGISTER) },
                modifier = Modifier.fillMaxWidth()
            )
            Column(modifier = Modifier.padding(top = 14.dp)) {
                WideOutlinedButton(
                    text = "Керү",
                    onClick = { navController.navigate(Routes.LOGIN) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

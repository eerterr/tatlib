package com.tatlib.app.ui.onboarding

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.KazanSunsetHero
import com.tatlib.app.ui.components.LanguageToggle
import com.tatlib.app.ui.components.PrimaryButton
import com.tatlib.app.ui.navigation.Routes

@Composable
fun WelcomeScreen(navController: NavHostController, viewModel: AppViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        KazanSunsetHero(modifier = Modifier.fillMaxWidth().height(340.dp))

        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                LanguageToggle(
                    language = viewModel.language,
                    onToggle = viewModel::toggleLanguage,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    "Рәхим итегез!",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Телне белү - дөньяны башкача күрү",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 12.dp, bottom = 36.dp)
                )
                PrimaryButton(
                    text = "Башлау",
                    onClick = { navController.navigate(Routes.AUTH_CHOICE) },
                    modifier = Modifier.padding(bottom = 40.dp)
                )
            }
        }
    }
}

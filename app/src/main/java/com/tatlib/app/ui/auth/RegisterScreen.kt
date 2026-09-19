package com.tatlib.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.FilterChip
import com.tatlib.app.ui.components.ScreenTopBar
import com.tatlib.app.ui.components.WideButton
import com.tatlib.app.ui.navigation.Routes

private val ageBrackets = listOf("10 - 14", "14 - 18", "18 - 25", "25+")

@Composable
fun RegisterScreen(navController: NavHostController, viewModel: AppViewModel) {
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var selectedAge by remember { mutableStateOf(viewModel.selectedAgeBracket ?: "18 - 25") }

    val passwordsMismatch = passwordConfirm.isNotEmpty() && password != passwordConfirm
    val canSubmit = name.isNotBlank() && contact.isNotBlank() &&
        password.length >= 4 && !passwordsMismatch

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp)
    ) {
        ScreenTopBar(
            onBack = { navController.popBackStack() },
            showLanguageToggle = false,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Text(
            "Теркәлү",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 12.dp, bottom = 28.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Исем") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
        OutlinedTextField(
            value = contact,
            onValueChange = { contact = it },
            label = { Text("Телефон номеры / электрон почта") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Серсүз") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )
        OutlinedTextField(
            value = passwordConfirm,
            onValueChange = { passwordConfirm = it },
            label = { Text("Серсүзне кабатлагыз") },
            singleLine = true,
            isError = passwordsMismatch,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )
        if (passwordsMismatch) {
            Text(
                "Серсүзләр туры килми",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Text(
            "Сезнең яшь",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ageBrackets.forEach { bracket ->
                FilterChip(
                    text = bracket,
                    selected = selectedAge == bracket,
                    onClick = { selectedAge = bracket }
                )
            }
        }

        WideButton(
            text = "Теркәлү",
            enabled = canSubmit,
            onClick = {
                viewModel.selectedAgeBracket = selectedAge
                viewModel.completeRegistration(name)
                navController.navigate(Routes.LEVEL_INTRO)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 32.dp)
        )
    }
}

package com.tatlib.app.ui.leveltest

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.data.MockData
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.PrimaryButton
import com.tatlib.app.ui.components.ScreenTopBar
import com.tatlib.app.ui.navigation.Routes

@Composable
fun LevelQuizScreen(navController: NavHostController, viewModel: AppViewModel) {
    val questions = MockData.levelQuiz
    var questionIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember(questionIndex) { mutableStateOf<Int?>(null) }

    val question = questions[questionIndex]
    val progress = (questionIndex + 1f) / questions.size

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

        Text(
            "Определи свой уровень татарского",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )

        Text(
            "${questionIndex + 1} / ${questions.size}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 30.dp)
                .clip(RoundedCornerShape(8.dp)),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            question.prompt,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 28.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            question.options.forEachIndexed { index, option ->
                val selected = selectedOption == index
                Text(
                    option.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { selectedOption = index }
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                )
            }
        }

        androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))

        PrimaryButton(
            text = if (questionIndex == questions.lastIndex) "Тәмамлау" else "Алга",
            enabled = selectedOption != null,
            onClick = {
                val correct = question.options[selectedOption!!].isCorrect
                viewModel.recordQuizAnswer(questionIndex, correct)
                if (questionIndex == questions.lastIndex) {
                    viewModel.finishQuizAndComputeLevel()
                    navController.navigate(Routes.LEVEL_RESULT)
                } else {
                    questionIndex += 1
                }
            },
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}

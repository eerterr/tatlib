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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.R
import com.tatlib.app.data.MockData
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.ActionButton
import com.tatlib.app.ui.components.ActionStyle
import com.tatlib.app.ui.components.DecorArc
import com.tatlib.app.ui.components.DecorStar
import com.tatlib.app.ui.components.EmptyState
import com.tatlib.app.ui.components.ProgressLine
import com.tatlib.app.ui.components.QuizOption
import com.tatlib.app.ui.components.TopBar
import com.tatlib.app.ui.components.TopBarLeft
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.theme.tatlibColors

@Composable
fun LevelQuizScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    val questions = MockData.levelQuiz
    var questionIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember(questionIndex) { mutableStateOf<Int?>(null) }
    var correctAnswers by remember { mutableIntStateOf(0) }
    val colors = MaterialTheme.tatlibColors

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clipToBounds()
    ) {
        Box(Modifier.fillMaxSize().statusBarsPadding()) {
            // Quiz.dc.html: одна дуга sky2 (250, −420) и две звезды 28 sky2 (330, 118) · 14 peach2 (300, 160).
            DecorArc(520.dp, colors.sky2, Modifier.offset(x = 250.dp, y = (-420).dp))
            DecorStar(28.dp, colors.sky2, Modifier.offset(x = 330.dp, y = 118.dp))
            DecorStar(14.dp, colors.peach2, Modifier.offset(x = 300.dp, y = 160.dp))

            Column(Modifier.fillMaxSize()) {
                // Счётчик в макете — оверлайн `.ov muted`, а не titleMedium заголовка TopBar: кладём поверх шапки.
                Box {
                    TopBar(left = TopBarLeft.Back, onLeft = { navController.popBackStack() })
                    if (questions.isNotEmpty()) {
                        Text(
                            stringResource(R.string.quiz_counter, questionIndex + 1, questions.size).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.inkMuted,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                if (questions.isEmpty()) {
                    EmptyState(title = stringResource(R.string.quiz_empty), text = "")
                    return@Column
                }

                val question = questions[questionIndex]
                val last = questionIndex == questions.lastIndex

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    ProgressLine((questionIndex + 1f) / questions.size)
                    Text(
                        stringResource(R.string.quiz_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        question.prompt,
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.inkSoft
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        question.options.forEachIndexed { index, option ->
                            QuizOption(
                                text = option.text,
                                selected = selectedOption == index,
                                onClick = { selectedOption = index }
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    val enabled = selectedOption != null
                    ActionButton(
                        text = stringResource(if (last) R.string.quiz_finish else R.string.action_forward),
                        style = ActionStyle.Primary,
                        alignEnd = true,
                        // Неактивное состояние в макете не нарисовано: приглушаем круг, клик без выбора игнорируется.
                        modifier = Modifier.align(Alignment.End).alpha(if (enabled) 1f else 0.45f),
                        onClick = {
                            val selected = selectedOption ?: return@ActionButton
                            if (question.options[selected].isCorrect) correctAnswers++
                            if (last) {
                                navController.navigate(Routes.LEVEL_RESULT) {
                                    popUpTo(Routes.LEVEL_QUIZ) { inclusive = true }
                                }
                            } else {
                                questionIndex++
                            }
                        }
                    )
                }
            }
        }
    }
}

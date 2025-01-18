package com.example.scoutquest.ui.views.gamesession.tasktypes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scoutquest.data.models.tasktypes.TrueFalse
import com.example.scoutquest.utils.AnswersChecker
import com.example.scoutquest.viewmodels.gamesession.GameSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrueFalseView(trueFalse: TrueFalse, viewModel: GameSessionViewModel, onComplete: (Int) -> Unit) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userAnswers by remember { mutableStateOf<List<Boolean>>(emptyList()) }
    var showResult by remember { mutableStateOf(false) }
    var trueFalseResult by remember { mutableStateOf<AnswersChecker.TrueFalseResult?>(null) }

    val currentQuestion = trueFalse.questionsTf.getOrNull(currentQuestionIndex)
    val answersChecker = AnswersChecker()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("True/False Task") })
        },
        content = { paddingValues ->
            if (showResult) {
                trueFalseResult?.let { result ->
                    viewModel.updateTaskScore(result.points)
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Task Completed! You scored ${result.points} points.",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (result.incorrectAnswers.isNotEmpty()) {
                            Text("You made mistakes in the following questions:")
                            result.incorrectAnswers.forEach { questionIndex ->
                                val questionText = trueFalse.questionsTf[questionIndex]
                                val correctAnswer = if (trueFalse.answersTf[questionIndex]) "True" else "False"
                                Text("- Question: $questionText")
                                Text("  Correct answer: $correctAnswer")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { onComplete(result.points) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Continue")
                        }
                    }
                }
            } else {
                currentQuestion?.let { question ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = question,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Button(onClick = {
                                userAnswers = userAnswers + true
                                if (currentQuestionIndex < trueFalse.questionsTf.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    trueFalseResult = answersChecker.checkTrueFalse(trueFalse, userAnswers + true)
                                    showResult = true
                                }
                            }) {
                                Text("True")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(onClick = {
                                userAnswers = userAnswers + false
                                if (currentQuestionIndex < trueFalse.questionsTf.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    trueFalseResult = answersChecker.checkTrueFalse(trueFalse, userAnswers + false)
                                    showResult = true
                                }
                            }) {
                                Text("False")
                            }
                        }
                    }
                }
            }
        }
    )
}

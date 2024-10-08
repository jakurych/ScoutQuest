package com.example.scoutquest.ui.views.gamesession.tasktypes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scoutquest.data.models.tasktypes.Question
import com.example.scoutquest.data.models.tasktypes.Quiz
import com.example.scoutquest.utils.AnswersChecker
import com.example.scoutquest.viewmodels.gamesession.GameSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizView(quiz: Quiz, viewModel: GameSessionViewModel, onComplete: () -> Unit) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswers by remember { mutableStateOf<List<Int>>(emptyList()) }
    var showResult by remember { mutableStateOf(false) }
    val incorrectAnswers = remember { mutableListOf<Pair<Int, List<Int>>>() }

    val currentQuestion = quiz.questions.getOrNull(currentQuestionIndex)
    val answersChecker = AnswersChecker()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Quiz Task") })
        },
        content = { paddingValues ->
            if (showResult) {
                val points = answersChecker.checkQuiz(quiz, listOf(selectedAnswers))
                viewModel.updateTaskScore(points) // Aktualizacja punktów w ViewModel
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Quiz Completed! You scored $points points.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (incorrectAnswers.isNotEmpty()) {
                        Text("You made mistakes in the following questions:")
                        incorrectAnswers.forEach { (questionIndex, correctAnswer) ->
                            val questionText = quiz.questions[questionIndex].questionText
                            val correctOptions = correctAnswer.joinToString(", ") { quiz.questions[questionIndex].options[it] }
                            Text("- Question: $questionText")
                            Text("  Correct answer(s): $correctOptions")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Continue")
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
                            text = question.questionText,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        question.options.forEachIndexed { index, option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = selectedAnswers.contains(index),
                                    onClick = {
                                        selectedAnswers = listOf(index)
                                    }
                                )
                                Text(text = option)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                if (selectedAnswers.sorted() != question.correctAnswerIndex.sorted()) {
                                    incorrectAnswers.add(currentQuestionIndex to question.correctAnswerIndex)
                                }
                                if (currentQuestionIndex < quiz.questions.size - 1) {
                                    currentQuestionIndex++
                                    selectedAnswers = emptyList()
                                } else {
                                    showResult = true
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(if (currentQuestionIndex < quiz.questions.size - 1) "Next" else "Finish")
                        }
                    }
                }
            }
        }
    )
}



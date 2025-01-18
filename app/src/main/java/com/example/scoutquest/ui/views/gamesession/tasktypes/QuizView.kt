package com.example.scoutquest.ui.views.gamesession.tasktypes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scoutquest.data.models.tasktypes.Quiz
import com.example.scoutquest.utils.AnswersChecker
import com.example.scoutquest.viewmodels.gamesession.GameSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizView(quiz: Quiz, viewModel: GameSessionViewModel, onComplete: (Int) -> Unit) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswers by remember { mutableStateOf<List<List<Int>>>(List(quiz.questions.size) { emptyList() }) }
    var showResult by remember { mutableStateOf(false) }
    var quizResult by remember { mutableStateOf<AnswersChecker.QuizResult?>(null) }

    val currentQuestion = quiz.questions.getOrNull(currentQuestionIndex)
    val answersChecker = AnswersChecker()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Quiz Task") })
        },
        content = { paddingValues ->
            if (showResult) {
                quizResult?.let { result ->
                    viewModel.updateTaskScore(result.points)
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Quiz Completed! You scored ${result.points} points.",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (result.incorrectAnswers.isNotEmpty()) {
                            Text("You made mistakes in the following questions:")
                            result.incorrectAnswers.forEach { (questionIndex, correctAnswer) ->
                                val questionText = quiz.questions[questionIndex].questionText
                                val correctOptions = correctAnswer.joinToString(", ") {
                                    quiz.questions[questionIndex].options[it]
                                }
                                Text("- Question: $questionText")
                                Text("  Correct answer(s): $correctOptions")
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
                            text = question.questionText,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        question.options.forEachIndexed { index, option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = selectedAnswers[currentQuestionIndex].contains(index),
                                    onCheckedChange = { isChecked ->
                                        val currentAnswers = selectedAnswers[currentQuestionIndex].toMutableList()
                                        if (isChecked) {
                                            if (!currentAnswers.contains(index)) {
                                                currentAnswers.add(index)
                                            }
                                        } else {
                                            currentAnswers.remove(index)
                                        }
                                        val newAnswers = selectedAnswers.toMutableList()
                                        newAnswers[currentQuestionIndex] = currentAnswers
                                        selectedAnswers = newAnswers
                                    }
                                )
                                Text(text = option)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                if (currentQuestionIndex < quiz.questions.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    quizResult = answersChecker.checkQuiz(quiz, selectedAnswers)
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
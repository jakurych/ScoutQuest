@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.scoutquest.ui.views.gamesession.tasktypes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scoutquest.data.models.tasktypes.Question
import com.example.scoutquest.data.models.tasktypes.Quiz

@Composable
fun QuizView(quiz: Quiz, onComplete: () -> Unit) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswers by remember { mutableStateOf<List<Int>>(emptyList()) }
    var showResult by remember { mutableStateOf(false) }

    val currentQuestion = quiz.questions.getOrNull(currentQuestionIndex)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Quiz Task") })
        },
        content = { paddingValues ->
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
            } ?: run {
                if (showResult) {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Quiz Completed!",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onComplete,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Continue")
                        }
                    }
                }
            }
        }
    )
}

package com.example.scoutquest.ui.views.tasktypes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scoutquest.data.models.tasktypes.Question
import com.example.scoutquest.data.models.tasktypes.Quiz
import com.example.scoutquest.viewmodels.tasktypes.QuizViewModel

@Composable
fun CreateQuizView(
    quizViewModel: QuizViewModel,
    onBack: () -> Unit,
    onSaveQuiz: @Composable (Quiz) -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(List(2) { "" }) }
    var correctAnswerIndex by remember { mutableStateOf(0) }
    var correctAnswerExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Question Text") }
        )

        options.forEachIndexed { index, option ->
            TextField(
                value = option,
                onValueChange = { newOption ->
                    options = options.toMutableList().apply { set(index, newOption) }
                },
                label = { Text("Option ${index + 1}") }
            )
        }

        Button(onClick = {
            if (options.size < 5) {
                options = options + ""
            }
        }) {
            Text("Add Option")
        }

        Button(onClick = {
            if (options.size > 2) {
                options = options.dropLast(1)
            }
        }) {
            Text("Remove Option")
        }

        Box {
            Button(onClick = { correctAnswerExpanded = true }) {
                Text("Select Correct Answer")
            }
            DropdownMenu(
                expanded = correctAnswerExpanded,
                onDismissRequest = { correctAnswerExpanded = false }
            ) {
                options.forEachIndexed { index, _ ->
                    DropdownMenuItem(
                        text = { Text("Correct Answer: ${index + 1}") },
                        onClick = {
                            correctAnswerIndex = index
                            correctAnswerExpanded = false
                        }
                    )
                }
            }
        }

        Button(onClick = {
            val question = Question(questionText, options, correctAnswerIndex)
            quizViewModel.addQuestion(question)
            questionText = ""
            options = List(2) { "" }
            correctAnswerIndex = 0
        }) {
            Text("Add Question")
        }

        Button(
            onClick = {
                val quiz = Quiz(questions = quizViewModel.questions.value)
               // onSaveQuiz(quiz)
                quizViewModel.resetQuestions()
            }
        ) {
            Text("Save Quiz Task")
        }

        Button(onClick = onBack) {
            Text("Cancel")
        }
    }
}

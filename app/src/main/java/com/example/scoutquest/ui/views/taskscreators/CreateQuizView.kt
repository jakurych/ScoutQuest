package com.example.scoutquest.ui.views.taskscreators

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scoutquest.data.models.tasktypes.Question
import com.example.scoutquest.ui.navigation.AddTask
import com.example.scoutquest.viewmodels.tasktypes.QuizViewModel
import com.example.scoutquest.ui.theme.button_green
import com.example.scoutquest.ui.theme.drab_dark_brown

@Composable
fun CreateQuizView(
    quizViewModel: QuizViewModel,
    navController: NavController
) {
    var questionText by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(List(2) { "" }) }
    var correctAnswerIndices by remember { mutableStateOf<List<Int>>(emptyList()) }
    val hasQuestions by quizViewModel.hasQuestions.collectAsState()
    val questions by quizViewModel.questions.collectAsState()

    var isReorderingEnabled by remember { mutableStateOf(false) }
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    fun calculateNewIndex(draggedIndex: Int, dragOffsetY: Float): Int {
        val newIndex = draggedIndex + (dragOffsetY / 150).toInt()
        return newIndex.coerceIn(0, questions.size - 1)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            BasicTextField(
                value = questionText,
                onValueChange = { questionText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(drab_dark_brown)
                    .padding(8.dp),
                textStyle = TextStyle(color = Color.White),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.padding(8.dp)) {
                        if (questionText.isEmpty()) Text("Enter question text", color = Color.White)
                        innerTextField()
                    }
                }
            )
        }

        itemsIndexed(options) { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicTextField(
                    value = option,
                    onValueChange = { newOption ->
                        options = options.toMutableList().apply { set(index, newOption) }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .background(drab_dark_brown)
                        .padding(8.dp),
                    textStyle = TextStyle(color = Color.White),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.padding(8.dp)) {
                            if (option.isEmpty()) Text("Enter option ${index + 1}", color = Color.White)
                            innerTextField()
                        }
                    }
                )
                Checkbox(
                    checked = index in correctAnswerIndices,
                    onCheckedChange = {
                        correctAnswerIndices = if (it) {
                            correctAnswerIndices + index
                        } else {
                            correctAnswerIndices - index
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        item {
            Button(
                onClick = {
                    if (options.size < 5) {
                        options = options + ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = button_green)
            ) {
                Text("Add Option", color = Color.White)
            }
        }

        item {
            Button(
                onClick = {
                    if (options.size > 2) {
                        options = options.dropLast(1)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = button_green)
            ) {
                Text("Remove Option", color = Color.White)
            }
        }

        item {
            Button(
                onClick = {
                    val question = Question(questionText, options, correctAnswerIndices)
                    quizViewModel.addQuestion(question)
                    questionText = ""
                    options = List(2) { "" }
                    correctAnswerIndices = emptyList()
                },
                enabled = questionText.isNotBlank() && options.size >= 2 && correctAnswerIndices.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (questionText.isNotBlank() && options.size >= 2 && correctAnswerIndices.isNotEmpty()) button_green else Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text("Add Question", color = Color.White)
            }
        }

        itemsIndexed(questions) { index, question ->
            val isDragging = draggedItemIndex == index
            val elevation by animateFloatAsState(targetValue = if (isDragging) 16.dp.value else 4.dp.value)
            val scale by animateFloatAsState(targetValue = if (isDragging) 1.1f else 1f)

            val newIndex = draggedItemIndex?.let { calculateNewIndex(it, dragOffset.y) }
            val actualOffset = if (newIndex != null && index == newIndex && draggedItemIndex != index) {
                if (newIndex < draggedItemIndex!!) Offset(0f, 150f) else Offset(0f, -150f)
            } else Offset.Zero
            val cardOffset by animateOffsetAsState(targetValue = if (isDragging) dragOffset else actualOffset)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .offset { IntOffset(cardOffset.x.toInt(), cardOffset.y.toInt()) }
                    .pointerInput(isReorderingEnabled) {
                        if (isReorderingEnabled) {
                            detectDragGestures(
                                onDragStart = { draggedItemIndex = index },
                                onDragEnd = {
                                    draggedItemIndex?.let { fromIndex ->
                                        val toIndex = calculateNewIndex(fromIndex, dragOffset.y)
                                        if (fromIndex != toIndex) {
                                            quizViewModel.swapQuestions(fromIndex, toIndex)
                                        }
                                    }
                                    draggedItemIndex = null
                                    dragOffset = Offset.Zero
                                },
                                onDragCancel = {
                                    draggedItemIndex = null
                                    dragOffset = Offset.Zero
                                },
                                onDrag = { change, dragAmount ->
                                    dragOffset += Offset(dragAmount.x, dragAmount.y)
                                    change.consume()
                                }
                            )
                        }
                    }
                    .scale(scale),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
                colors = CardDefaults.cardColors(containerColor = drab_dark_brown)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Question: ${question.questionText}", color = Color.White)
                    question.options.forEachIndexed { optIndex, answer ->
                        Text("Option ${optIndex + 1}: $answer ${if (optIndex in question.correctAnswerIndex) "(Correct)" else ""}", color = Color.White)
                    }
                    Row {
                        IconButton(onClick = {
                            questionText = question.questionText
                            options = question.options.toMutableList()
                            correctAnswerIndices = question.correctAnswerIndex
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Question", tint = Color.White)
                        }
                        IconButton(onClick = {
                            quizViewModel.removeQuestion(index)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Question", tint = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    val quiz = quizViewModel.getCurrentQuiz()
                    if (quiz.questions.isNotEmpty()) {
                       // quizViewModel.saveCurrentQuiz()
                        //quizViewModel.setTaskDetailsEntered(true)
                        navController.popBackStack()
                    }
                },
                enabled = hasQuestions,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasQuestions) button_green else Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text("Save Quiz Task", color = Color.White)
            }


        }

        item {
            Button(
                onClick = {
                    isReorderingEnabled = !isReorderingEnabled
                },
                colors = ButtonDefaults.buttonColors(containerColor = button_green)
            ) {
                Text("Change Order", color = Color.White)
            }
        }

        item {
            Button(
                onClick = { navController.popBackStack()    },
                colors = ButtonDefaults.buttonColors(containerColor = button_green)
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    }
}
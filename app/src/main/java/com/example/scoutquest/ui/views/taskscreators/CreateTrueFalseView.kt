package com.example.scoutquest.ui.views.taskscreators

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scoutquest.ui.navigation.AddTask
import com.example.scoutquest.ui.theme.button_green
import com.example.scoutquest.ui.theme.drab_dark_brown
import com.example.scoutquest.viewmodels.tasktypes.TrueFalseViewModel

@Composable
fun CreateTrueFalseView(
    trueFalseViewModel: TrueFalseViewModel,
    navController: NavController
) {
    var questionText by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf(false) }
    val questionsTf by trueFalseViewModel.questionsTf.collectAsState()
    val answersTf by trueFalseViewModel.answersTf.collectAsState()

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

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Answer is True:", color = Color.White)
                Checkbox(
                    checked = answer,
                    onCheckedChange = { answer = it },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        item {
            Button(
                onClick = {
                    trueFalseViewModel.addQuestion(questionText, answer)
                    questionText = ""
                    answer = false
                },
                enabled = questionText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (questionText.isNotBlank()) button_green else Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text("Add Question", color = Color.White)
            }
        }

        itemsIndexed(questionsTf) { index, question ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = drab_dark_brown)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Question: $question", color = Color.White)
                    Text("Answer: ${if (answersTf[index]) "True" else "False"}", color = Color.White)
                    Row {
                        IconButton(onClick = {
                            questionText = question
                            answer = answersTf[index]
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Question", tint = Color.White)
                        }
                        IconButton(onClick = {
                            trueFalseViewModel.removeQuestion(index)
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
                    val trueFalse = trueFalseViewModel.getCurrentTrueFalse()
                    if (trueFalse.questionsTf.isNotEmpty()) {
                        trueFalseViewModel.saveCurrentTrueFalse()
                        trueFalseViewModel.setTaskDetailsEntered(true)
                        navController.navigate(AddTask)
                    }
                },
                enabled = questionsTf.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (questionsTf.isNotEmpty()) button_green else Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text("Save True/False Task", color = Color.White)
            }
        }

        item {
            Button(
                onClick = { navController.navigate(AddTask) },
                colors = ButtonDefaults.buttonColors(containerColor = button_green)
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    }
}

package com.example.scoutquest.ui.views.taskscreators

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
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
import com.example.scoutquest.viewmodels.tasktypes.OpenQuestionViewModel

@Composable
fun CreateOpenQuestionView(
    openQuestionViewModel: OpenQuestionViewModel,
    navController: NavController
) {
    var questionText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }

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
                        if (questionText.isEmpty()) Text("Enter open question text", color = Color.White)
                        innerTextField()
                    }
                }
            )
        }
        item {
            BasicTextField(
                value = answerText,
                onValueChange = { answerText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(drab_dark_brown)
                    .padding(8.dp),
                textStyle = TextStyle(color = Color.White),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.padding(8.dp)) {
                        if (answerText.isEmpty()) Text("Enter answer", color = Color.White)
                        innerTextField()
                    }
                }
            )
        }
        item {
            Button(
                onClick = {
                    openQuestionViewModel.setOpenQuestion(questionText, answerText)
                    navController.navigate(AddTask)
                },
                enabled = questionText.isNotBlank() && answerText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (questionText.isNotBlank() && answerText.isNotBlank()) button_green else Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text("Save Open Question", color = Color.White)
            }
        }
    }
}

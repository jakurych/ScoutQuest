package com.example.scoutquest.ui.views.taskscreators

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
    var currentTopic by remember { mutableStateOf("") }
    var expectedTopics by remember { mutableStateOf(listOf<String>()) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = currentTopic,
                    onValueChange = { currentTopic = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(drab_dark_brown)
                        .padding(8.dp),
                    textStyle = TextStyle(color = Color.White),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.padding(8.dp)) {
                            if (currentTopic.isEmpty()) Text("Enter expected topic", color = Color.White)
                            innerTextField()
                        }
                    }
                )
                IconButton(
                    onClick = {
                        if (currentTopic.isNotBlank()) {
                            expectedTopics = expectedTopics + currentTopic
                            currentTopic = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add topic")
                }
            }
        }

        items(expectedTopics) { topic ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(topic, modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { expectedTopics = expectedTopics - topic }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove topic")
                }
            }
        }

        item {
            Button(
                onClick = {
                    openQuestionViewModel.setOpenQuestion(questionText, expectedTopics)
                    navController.navigate(AddTask)
                },
                enabled = questionText.isNotBlank() && expectedTopics.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (questionText.isNotBlank() && expectedTopics.isNotEmpty()) button_green else Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text("Save Open Question", color = Color.White)
            }
        }
    }
}

package com.example.scoutquest.ui.views.gamesession.tasktypes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scoutquest.data.models.tasktypes.TrueFalse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrueFalseView(trueFalse: TrueFalse, onComplete: () -> Unit) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userAnswers by remember { mutableStateOf<List<Boolean>>(emptyList()) }
    var showResult by remember { mutableStateOf(false) }

    fun moveToNextOrFinish() {
        if (currentQuestionIndex < trueFalse.questionsTf.size - 1) {
            currentQuestionIndex++
        } else {
            showResult = true
        }
    }

    val currentQuestion = trueFalse.questionsTf.getOrNull(currentQuestionIndex)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("True/False Task") })
        },
        content = { paddingValues ->
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
                            moveToNextOrFinish()
                        }) {
                            Text("True")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = {
                            userAnswers = userAnswers + false
                            moveToNextOrFinish()
                        }) {
                            Text("False")
                        }
                    }
                }
            } ?: run {
                if (showResult) {
                    //result
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Task Completed!",
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

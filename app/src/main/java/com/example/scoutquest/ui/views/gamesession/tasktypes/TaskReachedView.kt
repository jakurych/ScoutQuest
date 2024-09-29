package com.example.scoutquest.ui.views.gamesession.tasktypes

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.scoutquest.data.models.Task

@Composable
fun TaskReachedView(task: Task, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Task Reached") },
        text = { Text(text = "You reached task number ${task.sequenceNumber}") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Ok")
            }
        }
    )
}

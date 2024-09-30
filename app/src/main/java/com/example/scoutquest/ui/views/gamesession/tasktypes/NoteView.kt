@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.scoutquest.ui.views.gamesession.tasktypes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scoutquest.data.models.tasktypes.Note

@Composable
fun NoteView(note: Note, onComplete: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Note Task") })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Notes:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                note.notes.forEach { noteItem ->
                    Text(
                        text = "• $noteItem",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Continue")
                }
            }
        }
    )
}

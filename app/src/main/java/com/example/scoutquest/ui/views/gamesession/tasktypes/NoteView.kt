package com.example.scoutquest.ui.views.gamesession.tasktypes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scoutquest.data.models.tasktypes.Note
import com.example.scoutquest.utils.AnswersChecker
import com.example.scoutquest.viewmodels.gamesession.GameSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteView(note: Note, viewModel: GameSessionViewModel, onComplete: (Int) -> Unit) {
    val answersChecker = AnswersChecker()
    var score by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) }

    LaunchedEffect(note) {
        score = answersChecker.checkNote(note)
        viewModel.updateTaskScore(score) // Aktualizacja punktów w ViewModel
        showResult = true
    }

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
                Text(
                    text = "You scored $score points.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onComplete(score) }, // Przekazywanie punktów do onComplete
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Continue")
                }
            }
        }
    )
}



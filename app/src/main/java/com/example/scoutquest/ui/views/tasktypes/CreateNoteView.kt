package com.example.scoutquest.ui.views.tasktypes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scoutquest.viewmodels.tasktypes.NoteViewModel
import com.example.scoutquest.ui.navigation.AddTask

@Composable
fun CreateNoteView(
    noteViewModel: NoteViewModel,
    navController: NavController
) {
    var noteText by remember { mutableStateOf("") }
    val notes by noteViewModel.notes.collectAsState()
    val hasNotes by noteViewModel.hasNotes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = noteText,
            onValueChange = { noteText = it },
            label = { Text("Note Text") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                noteViewModel.addNote(noteText)
                noteText = ""
            },
            enabled = noteText.isNotBlank()
        ) {
            Text("Add Note")
        }

        LazyColumn {
            itemsIndexed(notes) { index, note ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(note, modifier = Modifier.weight(1f))
                        IconButton(onClick = { noteViewModel.removeNote(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Note")
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if (hasNotes) {
                    noteViewModel.saveCurrentNote()
                    noteViewModel.setTaskDetailsEntered(true)
                    navController.navigate(AddTask)
                }
            },
            enabled = hasNotes
        ) {
            Text("Save Notes")
        }
    }
}

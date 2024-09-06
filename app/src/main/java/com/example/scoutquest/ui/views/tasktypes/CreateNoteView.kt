package com.example.scoutquest.ui.views.tasktypes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.scoutquest.viewmodels.tasktypes.NoteViewModel

@Composable
fun CreateNoteView(
    navController: NavController,
    noteViewModel: NoteViewModel,
    onSaveNote: (String) -> Unit
) {
    val noteText by noteViewModel.noteText.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BasicTextField(
            value = noteText,
            onValueChange = { noteViewModel.updateNoteText(it) },
            modifier = Modifier
                .fillMaxWidth()
                .background(drab_dark_brown)
                .padding(8.dp),
            textStyle = TextStyle(color = Color.White),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.padding(8.dp)) {
                    if (noteText.isEmpty()) Text("Write note", color = Color.White)
                    innerTextField()
                }
            }
        )

        Button(
            onClick = {
                if (noteText.isNotBlank()) {
                    onSaveNote(noteText)
                    noteViewModel.clearNoteText()
                    navController.navigate(AddTask)
                }
            },
            enabled = noteText.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (noteText.isNotBlank()) button_green else Color.Gray,
                contentColor = Color.White
            )
        ) {
            Text("Save note", color = Color.White)
        }

        Button(
            onClick = {
                noteViewModel.clearNoteText()
                navController.navigate(AddTask)
            },
            colors = ButtonDefaults.buttonColors(containerColor = button_green)
        ) {
            Text("Cancel", color = Color.White)
        }
    }
}

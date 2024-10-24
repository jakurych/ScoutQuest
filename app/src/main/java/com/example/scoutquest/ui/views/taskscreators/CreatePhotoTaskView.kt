package com.example.scoutquest.ui.views.taskscreators

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scoutquest.viewmodels.tasktypes.PhotoViewModel
import com.example.scoutquest.ui.navigation.AddTask

@Composable
fun CreatePhotoTaskView(
    photoViewModel: PhotoViewModel,
    navController: NavController
) {
    var instruction by remember { mutableStateOf("") }
    val hasInstruction by photoViewModel.hasInstruction.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = instruction,
            onValueChange = { instruction = it },
            label = { Text("Photo Task Instruction") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                photoViewModel.setInstruction(instruction)
                photoViewModel.saveCurrentPhotoTask()
                photoViewModel.setTaskDetailsEntered(true)
                navController.navigate(AddTask)
            },
            enabled = instruction.isNotBlank()
        ) {
            Icon(Icons.Default.Check, contentDescription = "Save")
            Text("Save Instruction")
        }
    }
}

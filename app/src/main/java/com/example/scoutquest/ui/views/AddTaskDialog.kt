package com.example.scoutquest.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scoutquest.data.models.Task
import android.location.Location
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    initialLocation: Location?,
    taskToEdit: Task? = null
) {
    var taskTitle by remember { mutableStateOf(taskToEdit?.taskTitle ?: "") }
    var taskDescription by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var taskPoints by remember { mutableStateOf(taskToEdit?.points?.toString() ?: "") }
    var selectedLocation by remember { mutableStateOf(taskToEdit?.location ?: initialLocation) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (taskToEdit == null) "Add Task" else "Edit Task") },
        text = {
            Column {
                TextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task Title") }
                )
                TextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Task Description") }
                )
                TextField(
                    value = taskPoints,
                    onValueChange = { taskPoints = it },
                    label = { Text("Task Points") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Location")
                val cameraPositionState = rememberCameraPositionState {
                    position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                        LatLng(37.7749, -122.4194), 10f
                    )
                }
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        selectedLocation = Location("").apply {
                            latitude = latLng.latitude
                            longitude = latLng.longitude
                        }
                    }
                ) {
                    selectedLocation?.let { location ->
                        Marker(
                            state = com.google.maps.android.compose.MarkerState(
                                position = LatLng(location.latitude, location.longitude)
                            ),
                            title = "Selected Location"
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedLocation?.let { location ->
                        val task = Task(
                            taskId = taskToEdit?.taskId ?: 0,
                            taskTitle = taskTitle,
                            gameId = taskToEdit?.gameId ?: 0,
                            description = taskDescription,
                            location = location,
                            points = taskPoints.toIntOrNull() ?: 0,
                            interactionType = taskToEdit?.interactionType ?: "",
                            status = taskToEdit?.status,
                            sequenceNumber = taskToEdit?.sequenceNumber ?: 0
                        )
                        onSave(task)
                        onDismiss()
                    }
                }
            ) {
                Text(if (taskToEdit == null) "Save" else "Update")
            }
        },
        dismissButton = {
            Row {
                if (taskToEdit != null) {
                    Button(
                        onClick = {
                            onDelete(taskToEdit)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

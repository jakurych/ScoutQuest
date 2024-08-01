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
import com.example.scoutquest.data.services.MarkersHelper
import com.example.scoutquest.utils.BitmapDescriptorUtils.rememberBitmapDescriptor

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    initialLocation: Location?,
    taskToEdit: Task? = null,
    onUpdateSequence: (Int, Int) -> Boolean,
    mapMarkers: List<Task>
) {
    var taskTitle by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var taskDescription by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var taskPoints by remember { mutableStateOf(taskToEdit?.points?.toString() ?: "") }
    var selectedLocation by remember { mutableStateOf(taskToEdit?.location ?: initialLocation) }
    var sequenceNumber by remember { mutableStateOf(taskToEdit?.sequenceNumber?.toString() ?: "") }
    var sequenceNumberError by remember { mutableStateOf(false) }
    var markerColor by remember { mutableStateOf(taskToEdit?.markerColor ?: "green") }

    val markerColors = listOf("red", "black", "blue", "green", "grey", "orange", "purple", "white", "yellow")
    var expanded by remember { mutableStateOf(false) }

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

                if (taskToEdit != null) {
                    TextField(
                        value = sequenceNumber,
                        onValueChange = { sequenceNumber = it },
                        label = { Text("Task Number") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        isError = sequenceNumberError
                    )
                    if (sequenceNumberError) {
                        Text(
                            text = "Invalid sequence number",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Marker Color")
                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(markerColor)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        markerColors.forEach { color ->
                            DropdownMenuItem(
                                text = { Text(color) },
                                onClick = {
                                    markerColor = color
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Location")
                val cameraPositionState = rememberCameraPositionState {
                    position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                        LatLng(52.253126, 20.900157), 10f
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
                    mapMarkers.forEachIndexed { index, task ->
                        val markerUrl = MarkersHelper.getMarkerUrl(task.markerColor ?: "green", (index + 1).toString())
                        val bitmapDescriptor = rememberBitmapDescriptor(markerUrl, index + 1)
                        task.location?.let { location ->
                            Marker(
                                state = com.google.maps.android.compose.MarkerState(position = LatLng(location.latitude, location.longitude)),
                                icon = bitmapDescriptor
                            )
                        }
                    }
                    selectedLocation?.let { location ->
                        val latLng = LatLng(location.latitude, location.longitude)
                        Marker(
                            state = com.google.maps.android.compose.MarkerState(position = latLng),
                            title = "Selected Location",
                            icon = rememberBitmapDescriptor(MarkersHelper.getMarkerUrl(markerColor, ""), 0)
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
                            title = taskTitle,
                            gameId = taskToEdit?.gameId ?: 0,
                            description = taskDescription,
                            location = location,
                            points = taskPoints.toIntOrNull() ?: 0,
                            sequenceNumber = taskToEdit?.sequenceNumber ?: 0,
                            markerColor = markerColor
                        )
                        if (taskToEdit != null) {
                            val newSequenceNumber = sequenceNumber.toIntOrNull()
                            if (newSequenceNumber != null && onUpdateSequence(task.taskId, newSequenceNumber)) {
                                onSave(task)
                                onDismiss()
                            } else {
                                sequenceNumberError = true
                            }
                        } else {
                            onSave(task)
                            onDismiss()
                        }
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

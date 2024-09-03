@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.scoutquest.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.scoutquest.data.models.Task
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.scoutquest.data.services.MarkersHelper
import com.example.scoutquest.utils.BitmapDescriptorUtils.rememberBitmapDescriptor
import com.example.scoutquest.ui.theme.*

@Composable
fun AddTaskView(
    onBack: () -> Unit,
    onSave: (Task) -> Unit,
    taskToEdit: Task? = null,
    initialLatitude: Double = 52.253126,
    initialLongitude: Double = 20.900157,
    mapMarkers: List<Task>
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val padding = screenWidth * 0.05f
    val elementSpacing = screenWidth * 0.02f

    var taskTitle by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var taskDescription by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var taskPoints by remember { mutableStateOf(taskToEdit?.points?.toString() ?: "") }
    var latitude by remember { mutableStateOf(taskToEdit?.latitude ?: initialLatitude) }
    var longitude by remember { mutableStateOf(taskToEdit?.longitude ?: initialLongitude) }
    var markerColor by remember { mutableStateOf(taskToEdit?.markerColor ?: "green") }

    val markerColors = listOf("red", "black", "blue", "green", "grey", "orange", "purple", "white", "yellow")
    var expanded by remember { mutableStateOf(false) }

    val taskTypes = listOf("Quiz")
    var selectedTaskType by remember { mutableStateOf(taskToEdit?.taskType ?: taskTypes.first()) }
    var taskTypeExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(elementSpacing)
    ) {
        TextField(
            value = taskTitle,
            onValueChange = { taskTitle = it },
            label = { Text("Task Title", color = Color.White) },
            textStyle = TextStyle(color = Color.White),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = drab_dark_brown,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                cursorColor = Color.White
            )
        )

        TextField(
            value = taskDescription,
            onValueChange = { taskDescription = it },
            label = { Text("Task Description", color = Color.White) },
            textStyle = TextStyle(color = Color.White),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = drab_dark_brown,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                cursorColor = Color.White
            )
        )

        TextField(
            value = taskPoints,
            onValueChange = { taskPoints = it },
            label = { Text("Task Points", color = Color.White) },
            textStyle = TextStyle(color = Color.White),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = drab_dark_brown,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                cursorColor = Color.White
            )
        )

        Text("Select Marker Color", color = Color.White)
        Box {
            OutlinedButton(onClick = { expanded = true }, colors = ButtonDefaults.buttonColors(containerColor = drab_dark_brown)) {
                Text(markerColor, color = Color.White)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                markerColors.forEach { color ->
                    DropdownMenuItem(
                        onClick = {
                            markerColor = color
                            expanded = false
                        },
                        text = { Text(color) }
                    )
                }
            }
        }

        Text("Select Task Type", color = Color.White)
        Box {
            OutlinedButton(onClick = { taskTypeExpanded = true }, colors = ButtonDefaults.buttonColors(containerColor = drab_dark_brown)) {
                Text(selectedTaskType, color = Color.White)
            }
            DropdownMenu(
                expanded = taskTypeExpanded,
                onDismissRequest = { taskTypeExpanded = false }
            ) {
                taskTypes.forEach { type ->
                    DropdownMenuItem(
                        onClick = {
                            selectedTaskType = type
                            taskTypeExpanded = false
                        },
                        text = { Text(type) }
                    )
                }
            }
        }

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            cameraPositionState = rememberCameraPositionState {
                position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                    LatLng(latitude, longitude), 10f)
            },
            onMapClick = { latLng ->
                latitude = latLng.latitude
                longitude = latLng.longitude
            }
        ) {
            mapMarkers.forEachIndexed { index, task ->
                val markerUrl = MarkersHelper.getMarkerUrl(task.markerColor, (index + 1).toString())
                val bitmapDescriptor = rememberBitmapDescriptor(markerUrl, index + 1)
                val position = LatLng(task.latitude, task.longitude)
                Marker(
                    state = com.google.maps.android.compose.MarkerState(position = position),
                    title = task.title,
                    icon = bitmapDescriptor
                )
            }
            Marker(
                state = com.google.maps.android.compose.MarkerState(position = LatLng(latitude, longitude)),
                title = "Selected Location",
                icon = rememberBitmapDescriptor(MarkersHelper.getMarkerUrl(markerColor, ""), 0)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.padding(elementSpacing),
                colors = ButtonDefaults.buttonColors(containerColor = button_green)
            ) {
                Text("Cancel", color = Color.White)
            }

            Button(
                onClick = {
                    val task = Task(
                        taskId = taskToEdit?.taskId ?: 0,
                        title = taskTitle,
                        description = taskDescription,
                        points = taskPoints.toIntOrNull() ?: 0,
                        latitude = latitude,
                        longitude = longitude,
                        markerColor = markerColor,
                        taskType = selectedTaskType
                    )
                    onSave(task)
                },
                modifier = Modifier.padding(elementSpacing),
                colors = ButtonDefaults.buttonColors(containerColor = button_green)
            ) {
                Text("Save Task", color = Color.White)
            }
        }
    }
}
package com.example.scoutquest.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.viewmodels.CreateNewGameViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.scoutquest.data.models.Task
import android.location.Location
import com.example.scoutquest.data.services.MarkersHelper
import com.example.scoutquest.utils.rememberBitmapDescriptor


@Composable
fun CreateNewGameView(viewModel: CreateNewGameViewModel) {

    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val isPublic by viewModel.isPublic.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3A3A3A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Header()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    BasicTextField(
                        value = name,
                        onValueChange = { viewModel.onNameChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.padding(8.dp)) {
                                if (name.isEmpty()) Text("Enter game name")
                                innerTextField()
                            }
                        }
                    )
                }

                item {
                    BasicTextField(
                        value = description,
                        onValueChange = { viewModel.onDescriptionChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.padding(8.dp)) {
                                if (description.isEmpty()) Text("Enter game description")
                                innerTextField()
                            }
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Public:")
                        Switch(
                            checked = isPublic,
                            onCheckedChange = { viewModel.onIsPublicChange(it) }
                        )
                    }
                }

                item {
                    val cameraPositionState = rememberCameraPositionState {
                        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(LatLng(37.7749, -122.4194), 10f)
                    }
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { latLng ->
                            val location = Location("").apply {
                                latitude = latLng.latitude
                                longitude = latLng.longitude
                            }
                            viewModel.onLocationSelected(location)
                        }
                    ) {
                        tasks.forEachIndexed { index, task ->
                            val markerUrl = MarkersHelper.getMarkerUrl("green", (index + 1).toString())
                            val bitmapDescriptor = rememberBitmapDescriptor(markerUrl, index + 1)
                            task.location?.let { location ->
                                Marker(
                                    state = com.google.maps.android.compose.MarkerState(position = LatLng(location.latitude, location.longitude)),
                                    title = task.title,
                                    icon = bitmapDescriptor
                                )
                            }
                        }
                    }
                }

                itemsIndexed(tasks) { index, task ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { taskToEdit = task; showAddTaskDialog = true },
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("Task ${index + 1}: ${task.title ?: "No Title"}")
                        Text("Description: ${task.description ?: "No Description"}")
                        Text("Points: ${task.points}")
                        IconButton(onClick = { taskToEdit = task; showAddTaskDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Task")
                        }
                    }
                }

                item {
                    Button(onClick = { showAddTaskDialog = true }) {
                        Text("Add Task")
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onSave = { task ->
                if (taskToEdit != null) {
                    viewModel.addTask(task)
                } else {
                    viewModel.addTask(task)
                }
                showAddTaskDialog = false
                taskToEdit = null
            },
            onDelete = { task ->
                viewModel.removeTask(task)
                showAddTaskDialog = false
                taskToEdit = null
            },
            initialLocation = selectedLocation,
            taskToEdit = taskToEdit,
            onUpdateSequence = { taskId, newSequenceNumber ->
                viewModel.updateTaskSequence(taskId, newSequenceNumber)
            }
        )
    }
}

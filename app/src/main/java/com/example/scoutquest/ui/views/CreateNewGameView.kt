package com.example.scoutquest.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.viewmodels.CreateNewGameViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import android.location.Location
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.scoutquest.data.models.Task

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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Public Game")
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
                        tasks.forEach { task ->
                            Marker(
                                state = com.google.maps.android.compose.MarkerState(position = LatLng(task.location.latitude, task.location.longitude)),
                                title = task.title
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            taskToEdit = null
                            showAddTaskDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Task")
                    }
                }

                items(tasks) { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.White)
                            .clickable {
                                taskToEdit = task
                                showAddTaskDialog = true
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Task number: ${task.sequenceNumber}")
                            Text("Title: ${task.title}")
                            Text("Description: ${task.description}")
                            Text("Points: ${task.points}")
                        }
                        IconButton(onClick = {
                            taskToEdit = task
                            showAddTaskDialog = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Task")
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            //   viewModel.saveGame()
                            // Assuming you have a navigation controller to navigate up
                            // navController.navigateUp()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Game")
                    }
                }
            }
        }

        if (showAddTaskDialog) {
            AddTaskDialog(
                onDismiss = { showAddTaskDialog = false },
                onSave = { task ->
                    viewModel.addTask(task)
                    showAddTaskDialog = false
                },
                onDelete = { task ->
                    viewModel.removeTask(task)
                    showAddTaskDialog = false
                },
                initialLocation = selectedLocation,
                taskToEdit = taskToEdit,
                onUpdateSequence = { taskId, newSequenceNumber ->
                    viewModel.updateTaskSequence(taskId, newSequenceNumber)
                }
            )
        }
    }
}

@Composable
fun Header() {
    // Your header implementation
}

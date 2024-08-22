package com.example.scoutquest.ui.views

import AddTaskDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.viewmodels.CreateNewGameViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.services.MarkersHelper
import com.example.scoutquest.utils.BitmapDescriptorUtils.rememberBitmapDescriptor
import com.example.scoutquest.ui.theme.*

@Composable
fun CreateNewGameView(viewModel: CreateNewGameViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val padding = screenWidth * 0.05f
    val elementSpacing = screenWidth * 0.02f

    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val isPublic by viewModel.isPublic.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val selectedLatitude by viewModel.selectedLatitude.collectAsState()
    val selectedLongitude by viewModel.selectedLongitude.collectAsState()
    val temporaryMarker by viewModel.temporaryMarker.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var isFullscreen by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(LatLng(52.253126, 20.900157), 10f)
    }

    val fullscreenCameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(LatLng(52.253126, 20.900157), 10f)
    }

    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Header()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(elementSpacing)
            ) {
                item {
                    BasicTextField(
                        value = name,
                        onValueChange = { viewModel.onNameChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(elementSpacing),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.padding(elementSpacing)) {
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
                            .padding(elementSpacing),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.padding(elementSpacing)) {
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
                            .padding(elementSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Public: ", color = Color.White)
                        Switch(
                            checked = isPublic,
                            onCheckedChange = { viewModel.onIsPublicChange(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = moss_green,
                                uncheckedThumbColor = Color.White,
                                checkedTrackColor = black_olive,
                                uncheckedTrackColor = black_olive
                            )
                        )
                    }
                }

                item {
                    Column {
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                            cameraPositionState = cameraPositionState,
                            onMapClick = { latLng ->
                                viewModel.onLocationSelected(latLng.latitude, latLng.longitude)
                            }
                        ) {
                            tasks.forEachIndexed { index, task ->
                                val markerUrl = MarkersHelper.getMarkerUrl(task.markerColor, (index + 1).toString())
                                val bitmapDescriptor = rememberBitmapDescriptor(markerUrl, index + 1)
                                val position = LatLng(task.latitude, task.longitude)
                                Marker(
                                    state = com.google.maps.android.compose.MarkerState(position = position),
                                    title = task.title,
                                    icon = bitmapDescriptor
                                )
                            }
                            temporaryMarker?.let { latLng ->
                                Marker(
                                    state = com.google.maps.android.compose.MarkerState(position = latLng),
                                    title = "Temporary Marker",
                                    icon = rememberBitmapDescriptor(MarkersHelper.getMarkerUrl("blue", ""), 0)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                isFullscreen = true
                                fullscreenCameraPositionState.position = cameraPositionState.position
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = elementSpacing),
                            colors = ButtonDefaults.buttonColors(containerColor = button_green)
                        ) {
                            Text("Full Screen Map", color = Color.White)
                        }

                        Button(
                            onClick = { showAddTaskDialog = true },
                            modifier = Modifier.fillMaxWidth().padding(top = elementSpacing),
                            colors = ButtonDefaults.buttonColors(containerColor = button_green)
                        ) {
                            Text("Add Task", color = Color.White)
                        }
                    }
                }

                itemsIndexed(tasks) { index, task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(elementSpacing)
                            .clickable { taskToEdit = task; showAddTaskDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = moss_green)
                    ) {
                        Column(
                            modifier = Modifier.padding(elementSpacing),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text("Task ${index + 1}: ${task.title ?: "No Title"}", color = Color.White)
                            Text("Description: ${task.description}", color = Color.White)
                            Text("Points: ${task.points}", color = Color.White)
                            IconButton(onClick = { taskToEdit = task; showAddTaskDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Task", tint = Color.White)
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { /* Logika do tworzenia gry */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(elementSpacing),
                        colors = ButtonDefaults.buttonColors(containerColor = button_green)
                    ) {
                        Text("Create game!", color = Color.White)
                    }
                }
            }
        }

        if (isFullscreen) {
            Dialog(
                onDismissRequest = { isFullscreen = false },
                properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = fullscreenCameraPositionState,
                        onMapClick = { latLng ->
                            viewModel.onLocationSelected(latLng.latitude, latLng.longitude)
                        }
                    ) {
                        tasks.forEachIndexed { index, task ->
                            val markerUrl = MarkersHelper.getMarkerUrl(task.markerColor, (index + 1).toString())
                            val bitmapDescriptor = rememberBitmapDescriptor(markerUrl, index + 1)
                            val position = LatLng(task.latitude, task.longitude)
                            Marker(
                                state = com.google.maps.android.compose.MarkerState(position = position),
                                title = task.title,
                                icon = bitmapDescriptor
                            )
                        }
                        temporaryMarker?.let { latLng ->
                            Marker(
                                state = com.google.maps.android.compose.MarkerState(position = latLng),
                                title = "Temporary Marker",
                                icon = rememberBitmapDescriptor(MarkersHelper.getMarkerUrl("blue", ""), 0)
                            )
                        }
                    }
                    Button(
                        onClick = {
                            isFullscreen = false
                            cameraPositionState.position = fullscreenCameraPositionState.position
                        },
                        modifier = Modifier.align(Alignment.TopEnd).padding(elementSpacing),
                        colors = ButtonDefaults.buttonColors(containerColor = moss_green)
                    ) {
                        Text("Close", color = Color.White)
                    }
                }
            }
        }

        if (showAddTaskDialog) {
            selectedLatitude?.let {
                selectedLongitude?.let { it1 ->
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
                        initialLatitude = it,
                        initialLongitude = it1,
                        taskToEdit = taskToEdit,
                        onUpdateSequence = { taskId, newSequenceNumber ->
                            viewModel.updateTaskSequence(taskId, newSequenceNumber)
                        },
                        mapMarkers = tasks
                    )
                }
            }
        }
    }
}

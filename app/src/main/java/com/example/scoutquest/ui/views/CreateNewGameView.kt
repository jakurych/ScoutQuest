package com.example.scoutquest.ui.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.IntOffset
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
import com.example.scoutquest.ui.theme.*
import com.example.scoutquest.utils.BitmapDescriptorUtils.rememberBitmapDescriptor

@Composable
fun CreateNewGameView(
    viewModel: CreateNewGameViewModel,
    onEditTask: (Task) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val padding = screenWidth * 0.05f
    val elementSpacing = screenWidth * 0.02f

    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val isPublic by viewModel.isPublic.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val isReorderingEnabled by viewModel.isReorderingEnabled.collectAsState()

    var isFullscreen by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(LatLng(52.253126, 20.900157), 10f)
    }

    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    fun calculateNewIndex(draggedIndex: Int, dragOffsetY: Float): Int {
        val newIndex = draggedIndex + (dragOffsetY / 150).toInt()
        return newIndex.coerceIn(0, tasks.size - 1)
    }

    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Header()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .let { if (isReorderingEnabled) it.border(BorderStroke(2.dp, Color.Yellow), RoundedCornerShape(8.dp)) else it },
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
                        }

                        Button(
                            onClick = {
                                isFullscreen = true
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = elementSpacing),
                            colors = ButtonDefaults.buttonColors(containerColor = button_green)
                        ) {
                            Text("Full Screen Map", color = Color.White)
                        }

                        Button(
                            onClick = {
                                onEditTask(Task())
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = elementSpacing),
                            colors = ButtonDefaults.buttonColors(containerColor = button_green)
                        ) {
                            Text("Add Task", color = Color.White)
                        }

                        Button(
                            onClick = {
                                viewModel.toggleReordering()
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = elementSpacing),
                            colors = ButtonDefaults.buttonColors(containerColor = button_green)
                        ) {
                            Text("Change Order", color = Color.White)
                        }
                    }
                }

                itemsIndexed(tasks) { index, task ->
                    val isDragging = draggedItemIndex == index
                    val elevation by animateFloatAsState(targetValue = if (isDragging) 16.dp.value else 4.dp.value)
                    val scale by animateFloatAsState(targetValue = if (isDragging) 1.1f else 1f)

                    val newIndex = draggedItemIndex?.let { calculateNewIndex(it, dragOffset.y) }
                    val actualOffset = if (newIndex != null && index == newIndex && draggedItemIndex != index) {
                        if (newIndex < draggedItemIndex!!) Offset(0f, 150f) else Offset(0f, -150f)
                    } else Offset.Zero
                    val cardOffset by animateOffsetAsState(targetValue = if (isDragging) dragOffset else actualOffset)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(elementSpacing)
                            .offset { IntOffset(cardOffset.x.toInt(), cardOffset.y.toInt()) }
                            .pointerInput(isReorderingEnabled) {
                                if (isReorderingEnabled) {
                                    detectDragGestures(
                                        onDragStart = {
                                            draggedItemIndex = index
                                        },
                                        onDragEnd = {
                                            draggedItemIndex?.let { fromIndex ->
                                                val toIndex = calculateNewIndex(fromIndex, dragOffset.y)
                                                if (fromIndex != toIndex) {
                                                    viewModel.moveTask(fromIndex, toIndex)
                                                }
                                            }
                                            draggedItemIndex = null
                                            dragOffset = Offset.Zero
                                        },
                                        onDragCancel = {
                                            draggedItemIndex = null
                                            dragOffset = Offset.Zero
                                        },
                                        onDrag = { change, dragAmount ->
                                            dragOffset += Offset(dragAmount.x, dragAmount.y)
                                            change.consume()
                                        }
                                    )
                                }
                            }
                            .clickable(enabled = !isReorderingEnabled) {
                                onEditTask(task)
                            }
                            .scale(scale),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
                        colors = CardDefaults.cardColors(containerColor = moss_green)
                    ) {
                        Column(
                            modifier = Modifier.padding(elementSpacing),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text("Task ${index + 1}: ${task.title ?: "No Title"}", color = Color.White)
                            Text("Description: ${task.description}", color = Color.White)
                            Text("Points: ${task.points}", color = Color.White)
                            Row {
                                IconButton(onClick = {
                                    onEditTask(task)
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Task", tint = Color.White)
                                }
                                IconButton(onClick = {
                                    viewModel.removeTask(task)
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = Color.White)
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { viewModel.saveGame() },
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
                    }
                }
            }
        }
    }
}

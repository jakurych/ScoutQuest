package com.example.scoutquest.ui.views.gamesession

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.services.MarkersHelper
import com.example.scoutquest.ui.views.gamesession.tasktypes.NoteView
import com.example.scoutquest.ui.views.gamesession.tasktypes.OpenQuestionView
import com.example.scoutquest.ui.views.gamesession.tasktypes.PhotoTaskView
import com.example.scoutquest.ui.views.gamesession.tasktypes.QuizView
import com.example.scoutquest.ui.views.gamesession.tasktypes.TrueFalseView
import com.example.scoutquest.utils.BitmapDescriptorUtils
import com.example.scoutquest.utils.BitmapDescriptorUtils.toBitmapDescriptor
import com.example.scoutquest.viewmodels.gamesession.GameSessionViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*


@Composable
fun OpenWorldMapView(
    viewModel: GameSessionViewModel,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    val scope = rememberCoroutineScope()

    val openWorldTasks by viewModel.openWorldTasks.collectAsState()
    val completedTasks by viewModel.completedOpenTasks.collectAsState()

    val userLocationState = remember { mutableStateOf<Location?>(null) }
    val showTaskInfo = remember { mutableStateOf(false) }
    val selectedTask = remember { mutableStateOf<Task?>(null) }
    val isTaskInRange = remember { mutableStateOf(false) }
    val showTaskView = remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val mapInitialized = remember { mutableStateOf(false) }

    // Ładowanie tasków
    LaunchedEffect(Unit) {
        viewModel.loadOpenWorldTasks()
    }

    // Obsługa lokalizacji użytkownika
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    userLocationState.value = location
                    if (!mapInitialized.value) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            15f
                        )
                        mapInitialized.value = true
                    }
                }
            }
        }
    }

    // Aktualizacja lokalizacji
    DisposableEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000L
            ).build()

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    Box {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            openWorldTasks.forEach { task ->
                val position = LatLng(task.latitude, task.longitude)
                val isCompleted = viewModel.isTaskCompleted(task.taskId.toString())

                var bitmapDescriptor by remember { mutableStateOf<BitmapDescriptor?>(null) }

                LaunchedEffect(task, isCompleted) {
                    val markerUrl = MarkersHelper.getColorMarkerUrl(task.markerColor)
                    val baseBitmap = BitmapDescriptorUtils.getBitmapFromUrl(context, markerUrl)
                    if (baseBitmap != null) {
                        val customBitmap = BitmapDescriptorUtils.createCustomMarkerBitmap(context, baseBitmap, 0) // przekazujemy 0 jako index, bo nie potrzebujemy numeru
                        bitmapDescriptor = customBitmap.toBitmapDescriptor()
                    }
                }


                Marker(
                    state = MarkerState(position = position),
                    title = task.title,
                    snippet = if (isCompleted) "Completed" else "Available",
                    icon = bitmapDescriptor ?: BitmapDescriptorFactory.defaultMarker(
                        if (isCompleted) BitmapDescriptorFactory.HUE_GREEN
                        else BitmapDescriptorFactory.HUE_RED
                    ),
                    onClick = { marker ->
                        if (!isCompleted) {
                            val userLocation = userLocationState.value
                            if (userLocation != null) {
                                val distance = FloatArray(1)
                                Location.distanceBetween(
                                    userLocation.latitude, userLocation.longitude,
                                    task.latitude, task.longitude,
                                    distance
                                )
                                isTaskInRange.value = distance[0] < 20
                                selectedTask.value = task
                                showTaskInfo.value = true
                            }
                        }
                        true
                    }
                )
            }

        }

        //Małe okienko informacyjne
        if (showTaskInfo.value && selectedTask.value != null) {
            val task = selectedTask.value!!
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                androidx.compose.material3.Card(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Task: ${task.title}")
                        Text("Category: ${task.category}")
                        //Text("Points: ${task.points}")
                        Text("Type: ${task.taskType}")

                        if (isTaskInRange.value) {
                            Button(
                                onClick = {
                                    showTaskInfo.value = false
                                    showTaskView.value = true
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Start Task")
                            }
                        } else {
                            Text("Get closer to start the task!")
                        }

                        Button(
                            onClick = { showTaskInfo.value = false },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }

        /// Wyświetlanie zadania
        if (showTaskView.value && selectedTask.value != null) {
            val task = selectedTask.value!!
            when {
                task.noteDetails != null -> {
                    val noteDetails = task.noteDetails
                    if (noteDetails != null) {
                        NoteView(note = noteDetails, viewModel = viewModel, onComplete = { points ->
                            showTaskView.value = false
                            viewModel.updateOpenTaskScore(task.taskId.toString(), points)
                        })
                    }
                }

                task.quizDetails != null -> {
                    val quizDetails = task.quizDetails
                    if (quizDetails != null) {
                        QuizView(quiz = quizDetails, viewModel = viewModel, onComplete = { points ->
                            showTaskView.value = false
                            viewModel.updateOpenTaskScore(task.taskId.toString(), points )
                        })
                    }
                }

                task.trueFalseDetails != null -> {
                    val trueFalseDetails = task.trueFalseDetails
                    if (trueFalseDetails != null) {
                        TrueFalseView(
                            trueFalse = trueFalseDetails,
                            viewModel = viewModel,
                            onComplete = { points ->
                                showTaskView.value = false
                                viewModel.updateOpenTaskScore(task.taskId.toString(), points )
                            })
                    }
                }

                task.openQuestionDetails != null -> {
                    val openQuestionDetails = task.openQuestionDetails
                    if (openQuestionDetails != null) {
                        OpenQuestionView(
                            openQuestion = openQuestionDetails,
                            viewModel = viewModel,
                            onComplete = { points ->
                                showTaskView.value = false
                                viewModel.updateOpenTaskScore(task.taskId.toString(), points)
                            }
                        )
                    }
                }

                task.photoDetails != null -> {
                    val photoDetails = task.photoDetails
                    if (photoDetails != null) {
                        PhotoTaskView(
                            photoTask = photoDetails,
                            viewModel = viewModel,
                            onComplete = { points ->
                                showTaskView.value = false
                                viewModel.updateOpenTaskScore(task.taskId.toString(), points )
                            }
                        )
                    }
                }
            }
        }



        if (!showTaskView.value) {
            Button(
                onClick = onExit,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Exit")
            }
        }
    }
}


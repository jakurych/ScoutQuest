package com.example.scoutquest.ui.views.gamesession

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.services.MarkersHelper
import com.example.scoutquest.ui.views.gamesession.tasktypes.EndGameView
import com.example.scoutquest.ui.views.gamesession.tasktypes.TaskReachedView
import com.example.scoutquest.utils.BitmapDescriptorUtils
import com.example.scoutquest.utils.BitmapDescriptorUtils.toBitmapDescriptor
import com.example.scoutquest.viewmodels.gamesession.GameSessionViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

@Composable
fun GameMapView(
    viewModel: GameSessionViewModel,
    onTaskReached: (Task) -> Unit,
    onGameEnd: () -> Unit
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val userLocationState = remember { mutableStateOf<Location?>(null) }
    val showTaskReachedView = remember { mutableStateOf(false) }
    val currentTask by remember { derivedStateOf { viewModel.getCurrentTask() } }
    val mapInitialized = remember { mutableStateOf(false) }

    var bitmapDescriptor by remember { mutableStateOf<BitmapDescriptor?>(null) }

    val wasUserInProximity = remember { mutableStateOf(false) }

    val gameEnded by remember { derivedStateOf { viewModel.gameEnded } }

    //Load marker when currentTask changes
    LaunchedEffect(currentTask) {
        bitmapDescriptor = null //Reset marker icon

        if (currentTask == null) {
            return@LaunchedEffect
        }

        currentTask?.let { task ->
            val index = task.sequenceNumber - 1
            val markerUrl = MarkersHelper.getMarkerUrl(task.markerColor, (index + 1).toString())

            //Load bitmapDescriptor asynchronously
            val baseBitmap = BitmapDescriptorUtils.getBitmapFromUrl(context, markerUrl)
            if (baseBitmap != null) {
                val customBitmap = BitmapDescriptorUtils.createCustomMarkerBitmap(context, baseBitmap, index + 1)
                bitmapDescriptor = customBitmap.toBitmapDescriptor()
            } else {
                Log.e("GameMapView", "Failed to load bitmap from URL: $markerUrl")
            }
        }
        //Reset proximity flag for new task
        wasUserInProximity.value = false
    }

    val locationCallback = remember {
        object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                val location = locationResult.lastLocation
                location?.let {
                    userLocationState.value = it
                    val userLatLng = LatLng(it.latitude, it.longitude)

                    if (!gameEnded && currentTask != null && !showTaskReachedView.value) {
                        val taskLatLng = LatLng(currentTask!!.latitude, currentTask!!.longitude)
                        val distance = FloatArray(1)
                        Location.distanceBetween(
                            userLatLng.latitude, userLatLng.longitude,
                            taskLatLng.latitude, taskLatLng.longitude,
                            distance
                        )
                        if (distance[0] < 20) {
                            if (!wasUserInProximity.value) {
                                showTaskReachedView.value = true
                                wasUserInProximity.value = true
                            }
                        } else {
                            wasUserInProximity.value = false
                        }
                    }

                    if (!mapInitialized.value) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 15f)
                        mapInitialized.value = true
                    }
                }
            }
        }
    }

    //Update user location
    DisposableEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000L
            ).apply {
                setMinUpdateIntervalMillis(2000L)
            }.build()

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    Box {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            if (!gameEnded) {
                currentTask?.let { task ->
                    val position = LatLng(task.latitude, task.longitude)

                    if (bitmapDescriptor != null) {
                        Marker(
                            state = MarkerState(position = position),
                            title = "Task ${task.sequenceNumber}",
                            snippet = task.title ?: "Task",
                            icon = bitmapDescriptor
                        )
                    } else {
                        Marker(
                            state = MarkerState(position = position),
                            title = "Task ${task.sequenceNumber}",
                            snippet = task.title ?: "Task"
                        )
                    }
                }
            }
        }

        if (gameEnded) {
            EndGameView(onDismiss = onGameEnd)
        } else if (showTaskReachedView.value && currentTask != null) {
            TaskReachedView(
                task = currentTask!!,
                onDismiss = {
                    showTaskReachedView.value = false
                    wasUserInProximity.value = false
                    onTaskReached(currentTask!!)
                    viewModel.onTaskReached(currentTask!!)
                    viewModel.advanceToNextTask()
                }
            )
        }
    }
}

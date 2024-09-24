package com.example.scoutquest.ui.views.gamesession

import android.location.Location
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.services.MarkersHelper
import com.example.scoutquest.utils.BitmapDescriptorUtils.rememberBitmapDescriptor
import com.example.scoutquest.viewmodels.gamesession.GameSessionViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun GameMapView(viewModel: GameSessionViewModel, onTaskReached: (Task) -> Unit) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    GoogleMap(
        cameraPositionState = cameraPositionState,
        onMyLocationClick = { location ->
            val userLatLng = LatLng(location.latitude, location.longitude)
            viewModel.getTasks().forEach { task ->
                val taskLatLng = LatLng(task.latitude, task.longitude)
                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLatLng.latitude, userLatLng.longitude,
                    taskLatLng.latitude, taskLatLng.longitude,
                    distance
                )
                if (distance[0] < 20) {
                    Toast.makeText(context, "You reached ${task.sequenceNumber}", Toast.LENGTH_LONG).show()
                    onTaskReached(task)
                }
            }
        }
    ) {
        viewModel.getTasks().forEachIndexed { index, task ->
            val markerUrl = MarkersHelper.getMarkerUrl(task.markerColor, (index + 1).toString())
            val bitmapDescriptor = rememberBitmapDescriptor(markerUrl, index + 1)
            val position = LatLng(task.latitude, task.longitude)
            if (bitmapDescriptor != null) {
                Marker(
                    state = MarkerState(position = position),
                    title = "Task ${task.sequenceNumber}",
                    snippet = task.title ?: "Task",
                    icon = bitmapDescriptor
                )
            }
        }
    }
}

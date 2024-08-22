import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scoutquest.data.models.Task
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.scoutquest.data.services.MarkersHelper
import com.example.scoutquest.utils.BitmapDescriptorUtils.rememberBitmapDescriptor
import com.example.scoutquest.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    initialLatitude: Double = 0.0,
    initialLongitude: Double = 0.0,
    taskToEdit: Task? = null,
    onUpdateSequence: (Int, Int) -> Boolean,
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
    var sequenceNumber by remember { mutableStateOf(taskToEdit?.sequenceNumber?.toString() ?: "") }
    var sequenceNumberError by remember { mutableStateOf(false) }
    var markerColor by remember { mutableStateOf(taskToEdit?.markerColor ?: "green") }
    var isFullscreen by remember { mutableStateOf(false) }

    val markerColors = listOf("red", "black", "blue", "green", "grey", "orange", "purple", "white", "yellow")
    var expanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                val cameraPositionState = rememberCameraPositionState {
                    position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                        LatLng(latitude, longitude), 10f
                    )
                }
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        latitude = latLng.latitude
                        longitude = latLng.longitude
                    }
                ) {
                    mapMarkers.forEachIndexed { index, task ->
                        val markerUrl = MarkersHelper.getMarkerUrl(task.markerColor ?: "green", (index + 1).toString())
                        val bitmapDescriptor = rememberBitmapDescriptor(markerUrl, index + 1)
                        Marker(
                            state = com.google.maps.android.compose.MarkerState(position = LatLng(task.latitude, task.longitude)),
                            icon = bitmapDescriptor
                        )
                    }
                    Marker(
                        state = com.google.maps.android.compose.MarkerState(position = LatLng(latitude, longitude)),
                        title = "Selected Location",
                        icon = rememberBitmapDescriptor(MarkersHelper.getMarkerUrl(markerColor, ""), 0)
                    )
                }
                Button(
                    onClick = { isFullscreen = false },
                    modifier = Modifier.align(Alignment.TopEnd).padding(padding),
                    colors = ButtonDefaults.buttonColors(containerColor = moss_green)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = drab_dark_brown,
            title = { Text(if (taskToEdit == null) "Add Task" else "Edit Task", color = Color.White) },
            text = {
                Column(
                    modifier = Modifier
                        .background(drab_dark_brown)
                        .verticalScroll(scrollState)
                        .padding(padding)
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
                            cursorColor = Color.White,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Red
                        )
                    )
                    Spacer(modifier = Modifier.height(elementSpacing))

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
                            cursorColor = Color.White,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Red
                        )
                    )
                    Spacer(modifier = Modifier.height(elementSpacing))

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
                            cursorColor = Color.White,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Red
                        )
                    )

                    if (taskToEdit != null) {
                        Spacer(modifier = Modifier.height(elementSpacing))

                        TextField(
                            value = sequenceNumber,
                            onValueChange = { sequenceNumber = it },
                            label = { Text("Task Number", color = Color.White) },
                            textStyle = TextStyle(color = Color.White),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            isError = sequenceNumberError,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = drab_dark_brown,
                                focusedIndicatorColor = Color.White,
                                unfocusedIndicatorColor = Color.White,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White,
                                cursorColor = Color.White,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Red
                            )
                        )
                        if (sequenceNumberError) {
                            Text(
                                text = "Invalid sequence number",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(elementSpacing))

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
                                    text = { Text(color) },
                                    onClick = {
                                        markerColor = color
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(elementSpacing))

                    Text("Select Location", color = Color.White)
                    val cameraPositionState = rememberCameraPositionState {
                        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                            LatLng(latitude, longitude), 10f
                        )
                    }
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { latLng ->
                            latitude = latLng.latitude
                            longitude = latLng.longitude
                        }
                    ) {
                        mapMarkers.forEachIndexed { index, task ->
                            val markerUrl = MarkersHelper.getMarkerUrl(task.markerColor ?: "green", (index + 1).toString())
                            val bitmapDescriptor = rememberBitmapDescriptor(markerUrl, index + 1)
                            Marker(
                                state = com.google.maps.android.compose.MarkerState(position = LatLng(task.latitude, task.longitude)),
                                icon = bitmapDescriptor
                            )
                        }
                        Marker(
                            state = com.google.maps.android.compose.MarkerState(position = LatLng(latitude, longitude)),
                            title = "Selected Location",
                            icon = rememberBitmapDescriptor(MarkersHelper.getMarkerUrl(markerColor, ""), 0)
                        )
                    }

                    Spacer(modifier = Modifier.height(elementSpacing))

                    Button(
                        onClick = { isFullscreen = true },
                        modifier = Modifier.fillMaxWidth().padding(top = elementSpacing),
                        colors = ButtonDefaults.buttonColors(containerColor = moss_green)
                    ) {
                        Text("Full Screen Map", color = Color.White)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val task = Task(
                            taskId = taskToEdit?.taskId ?: 0,
                            title = taskTitle,
                            gameId = taskToEdit?.gameId ?: 0,
                            description = taskDescription,
                            latitude = latitude,
                            longitude = longitude,
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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = moss_green)
                ) {
                    Text(if (taskToEdit == null) "Save" else "Update", color = Color.White)
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
                            Text("Delete", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(elementSpacing))
                    }
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = moss_green)) {
                        Text("Cancel", color = Color.White)
                    }
                }
            }
        )
    }
}

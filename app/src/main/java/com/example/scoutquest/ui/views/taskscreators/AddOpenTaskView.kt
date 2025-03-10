package com.example.scoutquest.ui.views.taskscreators

import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.services.MarkersHelper
import com.example.scoutquest.ui.components.CircleButton
import com.example.scoutquest.ui.navigation.CreateNote
import com.example.scoutquest.ui.navigation.CreateOpenQuestion
import com.example.scoutquest.ui.navigation.CreatePhotoTask
import com.example.scoutquest.ui.navigation.CreateQuiz
import com.example.scoutquest.ui.navigation.CreateTrueFalse
import com.example.scoutquest.ui.navigation.MainScreenRoute
import com.example.scoutquest.ui.theme.black_olive
import com.example.scoutquest.ui.theme.button_green
import com.example.scoutquest.ui.theme.drab_dark_brown
import com.example.scoutquest.utils.BitmapDescriptorUtils.rememberBitmapDescriptor
import com.example.scoutquest.viewmodels.gamesession.OpenTaskViewModel
import com.example.scoutquest.viewmodels.tasktypes.NoteViewModel
import com.example.scoutquest.viewmodels.tasktypes.OpenQuestionViewModel
import com.example.scoutquest.viewmodels.tasktypes.PhotoViewModel
import com.example.scoutquest.viewmodels.tasktypes.QuizViewModel
import com.example.scoutquest.viewmodels.tasktypes.TrueFalseViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOpenTaskView(
    viewModel: OpenTaskViewModel,
    navController: NavController,
    taskToEdit: Task? = null,
    mapMarkers: List<Task>,
    quizViewModel: QuizViewModel,
    noteViewModel: NoteViewModel,
    trueFalseViewModel: TrueFalseViewModel,
    openQuestionViewModel: OpenQuestionViewModel,
    photoViewModel: PhotoViewModel
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val padding = screenWidth * 0.05f
    val elementSpacing = screenWidth * 0.02f

    val taskTitle by viewModel.taskTitle.collectAsState()
    val taskDescription by viewModel.taskDescription.collectAsState()
    //val taskPoints by viewModel.taskPoints.collectAsState()
    val latitude by viewModel.latitude.collectAsState()
    val longitude by viewModel.longitude.collectAsState()
    val markerColor by viewModel.markerColor.collectAsState()
    val selectedTaskType by viewModel.selectedTaskType.collectAsState()
    val fullscreenCameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 10f)
    }

    var isMapFullScreen by remember { mutableStateOf(false) }
    var temporaryMarker by remember { mutableStateOf(LatLng(latitude, longitude)) }
    var taskTypeExpanded by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }


    val scrollState = rememberScrollState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 10f)
    }

    val taskTypes = listOf("Open question", "Quiz", "Note", "True/False", "Photo")
    val markerColors = listOf("red", "black", "blue", "green", "grey", "orange", "purple", "white", "yellow")
    var expanded by remember { mutableStateOf(false) }

    val hasQuizQuestions by quizViewModel.hasQuestions.collectAsState()
    val hasNotesNote by noteViewModel.hasNotes.collectAsState()
    val hasTrueFalseQuestions by trueFalseViewModel.hasQuestions.collectAsState()
    val hasOpenQuestion by openQuestionViewModel.hasOpenQuestion.collectAsState()
    val hasPhotoInstruction by photoViewModel.hasInstruction.collectAsState()



    val taskCategory by viewModel.taskCategory.collectAsState()

    LaunchedEffect(Unit) {
        /*taskToEdit?.let { task ->
            task.title?.let { viewModel.updateTitle(it) }  // viewModel.updateTitle(task.title)
            viewModel.updateDescription(task.description)
            viewModel.updatePoints(task.points.toString())
            viewModel.updateLatitude(task.latitude)
            viewModel.updateLongitude(task.longitude)
            viewModel.updateMarkerColor(task.markerColor)
            viewModel.updateSelectedTaskType(task.taskType ?: "None")
            task.category?.let { viewModel.updateCategory(it) }
        }*/


    }

    val selectedTaskTypeState by viewModel.selectedTaskType.collectAsState()
    val isTaskDetailsEntered = when (selectedTaskTypeState) {
        "Quiz" -> hasQuizQuestions
        "Note" -> hasNotesNote
        "True/False" -> hasTrueFalseQuestions
        "Open question" -> hasOpenQuestion
        "Photo" -> hasPhotoInstruction
        else -> false
    }

    LaunchedEffect(isTaskDetailsEntered) {
        viewModel.setTaskDetailsEntered(isTaskDetailsEntered)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(elementSpacing)
    ) {
        if (!isMapFullScreen) {
            TextField(
                value = taskTitle,
                onValueChange = { viewModel.updateTitle(it) },
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
                onValueChange = { viewModel.updateDescription(it) },
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
                value = taskCategory,
                onValueChange = { viewModel.updateCategory(it) },
                label = { Text("Enter category", color = Color.White) },
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

            /*TextField(
                value = taskPoints,
                onValueChange = { viewModel.updatePoints(it) },
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
            )*/

            Text("Select Marker Color", color = Color.White)
            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(containerColor = drab_dark_brown)
                ) {
                    Text(markerColor, color = Color.White)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    markerColors.forEach { color ->
                        DropdownMenuItem(
                            onClick = {
                                viewModel.updateMarkerColor(color)
                                expanded = false
                            },
                            text = { Text(color) }
                        )
                    }
                }
            }

            Text("Select Task Type", color = Color.White)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    OutlinedButton(
                        onClick = { taskTypeExpanded = true },
                        colors = ButtonDefaults.buttonColors(containerColor = drab_dark_brown)
                    ) {
                        Text(selectedTaskType, color = Color.White)
                    }
                    DropdownMenu(
                        expanded = taskTypeExpanded,
                        onDismissRequest = { taskTypeExpanded = false }
                    ) {
                        taskTypes.forEach { type ->
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.updateSelectedTaskType(type)
                                    taskTypeExpanded = false
                                },
                                text = { Text(type) }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        when (selectedTaskType) {
                            "Open question" -> {
                                openQuestionViewModel.setOpenQuestionFromTask(taskToEdit?.openQuestionDetails)
                                navController.navigate(CreateOpenQuestion)
                            }
                            "Quiz" -> {
                                quizViewModel.setQuestionsFromQuiz(taskToEdit?.quizDetails)
                                navController.navigate(CreateQuiz)
                            }
                            "Note" -> {
                                noteViewModel.setNotesFromNote(taskToEdit?.noteDetails)
                                navController.navigate(CreateNote)
                            }
                            "True/False" -> {
                                trueFalseViewModel.setQuestionsFromTrueFalse(taskToEdit?.trueFalseDetails)
                                navController.navigate(CreateTrueFalse)
                            }
                            "Photo" -> {
                                photoViewModel.setInstructionFromPhoto(taskToEdit?.photoDetails)
                                navController.navigate(CreatePhotoTask)
                            }
                        }
                    },
                    enabled = selectedTaskType != "None",
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedTaskType != "None") button_green else Color.Gray)
                ) {
                    Text("Add task details", color = Color.White)
                }
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    viewModel.updateLatitude(latLng.latitude)
                    viewModel.updateLongitude(latLng.longitude)
                    temporaryMarker = latLng
                }
            ) {
                mapMarkers.forEachIndexed { index, task ->
                    val markerUrl = MarkersHelper.getMarkerUrl(task.markerColor, (index + 1).toString())
                    val bitmapDescriptor = rememberBitmapDescriptor(markerUrl, index + 1)
                    val position = LatLng(task.latitude, task.longitude)
                    Marker(
                        state = MarkerState(position = position),
                        title = task.title,
                        icon = bitmapDescriptor
                    )
                }

                Marker(
                    state = MarkerState(position = temporaryMarker),
                    title = "Selected Location",
                    icon = rememberBitmapDescriptor(MarkersHelper.getMarkerUrl(markerColor, ""), 0)
                )
            }

            Button(
                onClick = { isMapFullScreen = true },
                modifier = Modifier.padding(elementSpacing),
                colors = ButtonDefaults.buttonColors(containerColor = button_green)
            ) {
                Text("Full Screen Map", color = Color.White)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(elementSpacing),
                    colors = ButtonDefaults.buttonColors(containerColor = button_green)
                ) {
                    Text("Cancel", color = Color.White)
                }

                Button(
                    onClick = {
                        if (!isTaskDetailsEntered) {
                            when (selectedTaskType) {
                                "Quiz" -> navController.navigate(CreateQuiz)
                                "Note" -> navController.navigate(CreateNote)
                                "True/False" -> navController.navigate(CreateTrueFalse)
                                "Open question" -> navController.navigate(CreateOpenQuestion)
                                "Photo" -> navController.navigate(CreatePhotoTask)
                            }
                        } else {
                            // Bezpośrednie wywołanie zapisu
                            viewModel.saveOpenTask(
                                quizDetails = if (selectedTaskType == "Quiz") quizViewModel.getCurrentQuiz() else null,
                                noteDetails = if (selectedTaskType == "Note") noteViewModel.getCurrentNote() else null,
                                trueFalseDetails = if (selectedTaskType == "True/False") trueFalseViewModel.getCurrentTrueFalse() else null,
                                openQuestionDetails = if (selectedTaskType == "Open question") openQuestionViewModel.getCurrentOpenQuestion() else null,
                                photoDetails = if (selectedTaskType == "Photo") photoViewModel.getCurrentPhotoTask() else null,
                                onSuccess = {
                                    showSuccessDialog = true
                                },
                                onFailure = {
                                    //obsluga błędów
                                }
                            )
                        }
                    },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTaskDetailsEntered) button_green else Color.Gray
                    ),
                    modifier = Modifier.padding(elementSpacing)
                ) {
                    Text(
                        if (isTaskDetailsEntered) "Save Open World Task" else "Add Task Details",
                        color = Color.White
                    )
                }

            }
        }
    }

    if (isMapFullScreen) {
        Dialog(
            onDismissRequest = { isMapFullScreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val focusManager = LocalFocusManager.current
            var searchQuery by remember { mutableStateOf("") }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = drab_dark_brown.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        placeholder = {
                            Text(
                                text = "Search address, location name, etc.",
                                style = TextStyle(
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                coroutineScope.launch {
                                    try {
                                        val geocoder = Geocoder(context)
                                        val addresses = geocoder.getFromLocationName(searchQuery, 1)

                                        if (addresses?.isNotEmpty() == true) {
                                            val address = addresses[0]
                                            val newLatLng = LatLng(address.latitude, address.longitude)

                                            //Aktualizacja ViewModel
                                            viewModel.updateLatitude(address.latitude)
                                            viewModel.updateLongitude(address.longitude)

                                            //Aktualizacja markera
                                            temporaryMarker = newLatLng

                                            //Aktualizacja pozycji kamery
                                            fullscreenCameraPositionState.position = CameraPosition.fromLatLngZoom(newLatLng, 15f)
                                        } else {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Error searching location", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        ),
                        textStyle = TextStyle(color = Color.White),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    focusManager.clearFocus()
                                    coroutineScope.launch {
                                        try {
                                            val geocoder = Geocoder(context)
                                            val addresses = geocoder.getFromLocationName(searchQuery, 1)

                                            if (addresses?.isNotEmpty() == true) {
                                                val address = addresses[0]
                                                val newLatLng = LatLng(address.latitude, address.longitude)

                                                //Aktualizacja ViewModel
                                                viewModel.updateLatitude(address.latitude)
                                                viewModel.updateLongitude(address.longitude)

                                                //Aktualizacja markera
                                                temporaryMarker = newLatLng

                                                //Aktualizacja pozycji kamery
                                                fullscreenCameraPositionState.position = CameraPosition.fromLatLngZoom(newLatLng, 15f)
                                            } else {
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } catch (e: IOException) {
                                            e.printStackTrace()
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "Error searching location", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.White
                                )
                            }
                        }
                    )
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    cameraPositionState = fullscreenCameraPositionState,
                    onMapClick = { latLng ->
                        viewModel.updateLatitude(latLng.latitude)
                        viewModel.updateLongitude(latLng.longitude)
                        temporaryMarker = latLng
                    }
                ) {
                    mapMarkers.forEachIndexed { index, task ->
                        val markerUrl = MarkersHelper.getMarkerUrl(task.markerColor, (index + 1).toString())
                        val bitmapDescriptor = rememberBitmapDescriptor(markerUrl, index + 1)
                        val position = LatLng(task.latitude, task.longitude)
                        Marker(
                            state = MarkerState(position = position),
                            title = task.title,
                            icon = bitmapDescriptor
                        )
                    }

                    Marker(
                        state = MarkerState(position = temporaryMarker),
                        title = "Selected Location",
                        icon = rememberBitmapDescriptor(
                            MarkersHelper.getMarkerUrl(markerColor, ""),
                            0
                        )
                    )
                }

                Button(
                    onClick = { isMapFullScreen = false },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = button_green)
                ) {
                    Text("Close Full Screen Map", color = Color.White)
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
            },
            title = {
                Text(text = "Success", color = Color.White)
            },
            text = {
                Text("Task has been successfully created!", color = Color.White)
            },
            confirmButton = {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircleButton(
                        text = "OK",
                        onClick = {
                            //Reset wszystkich ViewModeli
                            quizViewModel.resetQuiz()
                            noteViewModel.resetNote()
                            photoViewModel.resetPhotoTask()
                            trueFalseViewModel.resetTrueFalse()
                            openQuestionViewModel.resetOpenQuestion()
                            showSuccessDialog = false
                            //Powrót do głównego ekranu
                            navController.navigate(MainScreenRoute) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            containerColor = black_olive
        )
    }

}
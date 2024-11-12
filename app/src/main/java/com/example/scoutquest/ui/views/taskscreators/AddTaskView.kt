@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package com.example.scoutquest.ui.views.taskscreators

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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.services.MarkersHelper
import com.example.scoutquest.ui.navigation.CreateNote
import com.example.scoutquest.ui.navigation.CreateOpenQuestion
import com.example.scoutquest.ui.navigation.CreatePhotoTask
import com.example.scoutquest.ui.navigation.CreateQuiz
import com.example.scoutquest.ui.navigation.CreateTrueFalse
import com.example.scoutquest.ui.navigation.Creator
import com.example.scoutquest.ui.theme.button_green
import com.example.scoutquest.ui.theme.drab_dark_brown
import com.example.scoutquest.utils.BitmapDescriptorUtils.rememberBitmapDescriptor
import com.example.scoutquest.viewmodels.general.CreateNewGameViewModel
import com.example.scoutquest.viewmodels.tasktypes.NoteViewModel
import com.example.scoutquest.viewmodels.tasktypes.OpenQuestionViewModel
import com.example.scoutquest.viewmodels.tasktypes.PhotoViewModel
import com.example.scoutquest.viewmodels.tasktypes.QuizViewModel
import com.example.scoutquest.viewmodels.tasktypes.TrueFalseViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@ExperimentalMaterial3Api
@Composable
fun AddTaskView(
    viewModel: CreateNewGameViewModel,
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

    var taskTitle by remember { mutableStateOf(viewModel.currentTaskTitle) }
    var taskDescription by remember { mutableStateOf(viewModel.currentTaskDescription) }
    var taskPoints by remember { mutableStateOf(viewModel.currentTaskPoints) }
    var latitude by remember { mutableDoubleStateOf(viewModel.currentLatitude) }
    var longitude by remember { mutableDoubleStateOf(viewModel.currentLongitude) }
    var markerColor by remember { mutableStateOf("blue") }

    val fullscreenCameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
            LatLng(52.253126, 20.900157), 10f
        )
    }

    LaunchedEffect(Unit) {

        //task types Vms
        /*quizViewModel.setCreateNewGameViewModel(viewModel)
        noteViewModel.setCreateNewGameViewModel(viewModel)
        trueFalseViewModel.setCreateNewGameViewModel(viewModel)
        openQuestionViewModel.setCreateNewGameViewModel(viewModel)
        photoViewModel.setCreateNewGameViewModel(viewModel)
        */
        //vals from VM
        taskTitle = viewModel.currentTaskTitle
        taskDescription = viewModel.currentTaskDescription
        taskPoints = viewModel.currentTaskPoints
        latitude = viewModel.currentLatitude
        longitude = viewModel.currentLongitude

        //starting marker color
        if (viewModel.currentMarkerColor.isNotBlank()) {
            markerColor = viewModel.currentMarkerColor
        } else {
            markerColor = "blue"
            viewModel.currentMarkerColor = "blue"
        }

        if (taskToEdit != null) {
            viewModel.setTaskDetailsEntered(true)
        }
    }



    var temporaryMarker by remember { mutableStateOf(LatLng(latitude, longitude)) }

    val markerColors =
        listOf("red", "black", "blue", "green", "grey", "orange", "purple", "white", "yellow")
    var expanded by remember { mutableStateOf(false) }

    val taskTypes = listOf("Open question","Quiz", "Note","True/False" ,"Photo", "None")

    val selectedTaskType by viewModel.selectedTaskType.collectAsState()
    var taskTypeExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    var isMapFullScreen by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
            LatLng(52.253126, 20.900157), 10f
        )
    }

    //val isTaskDetailsEntered by viewModel.isTaskDetailsEntered.collectAsState()

    //spr czy zostały wprowadzone detale tasków
    val hasQuizQuestions by quizViewModel.hasQuestions.collectAsState()
    val hasNotesNote by noteViewModel.hasNotes.collectAsState()
    val hasTrueFalseQuestions by trueFalseViewModel.hasQuestions.collectAsState()
    val hasOpenQuestion by openQuestionViewModel.hasOpenQuestion.collectAsState()
    val hasPhotoInstruction by photoViewModel.hasInstruction.collectAsState()

    val selectedTaskTypeState by viewModel.selectedTaskType.collectAsState()
    val isTaskDetailsEntered = when (selectedTaskTypeState) {
        "Quiz" -> hasQuizQuestions
        "Note" -> hasNotesNote
        "True/False" -> hasTrueFalseQuestions
        "Open question" -> hasOpenQuestion
        "Photo" -> hasPhotoInstruction
        else -> false
    }


    fun updateViewModel() {
        viewModel.apply {
            currentTaskTitle = taskTitle
            currentTaskDescription = taskDescription
            currentTaskPoints = taskPoints
            currentLatitude = latitude
            currentLongitude = longitude
            currentMarkerColor = markerColor
        }
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
                onValueChange = {
                    taskTitle = it
                    updateViewModel()
                },
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
                onValueChange = {
                    taskDescription = it
                    updateViewModel()
                },
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
                onValueChange = {
                    taskPoints = it
                    updateViewModel()
                },
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
                                markerColor = color
                                updateViewModel()
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
                                    viewModel.setSelectedTaskType(type)
                                    taskTypeExpanded = false
                                },
                                text = { Text(type) }
                            )

                        }
                    }
                }

                Button(
                    onClick = {
                        updateViewModel()
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
                    .height(if (isMapFullScreen) LocalConfiguration.current.screenHeightDp.dp else 200.dp),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    latitude = latLng.latitude
                    longitude = latLng.longitude
                    temporaryMarker = latLng
                    updateViewModel()
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

            if (!isMapFullScreen) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.navigate(Creator) },
                        modifier = Modifier.padding(elementSpacing),
                        colors = ButtonDefaults.buttonColors(containerColor = button_green)
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    Button(
                        onClick = {
                            if (selectedTaskType == "Quiz" && !hasQuizQuestions) {
                                navController.navigate(CreateQuiz)
                            } else if (selectedTaskType == "Note" && !hasNotesNote) {
                                navController.navigate(CreateNote)
                            } else if (selectedTaskType == "True/False" && !hasTrueFalseQuestions) {
                                navController.navigate(CreateTrueFalse)
                            } else if(selectedTaskType == "Open question" && !hasOpenQuestion) {
                                navController.navigate(CreateOpenQuestion)
                            } else if (selectedTaskType == "Photo" && !hasPhotoInstruction) {
                                navController.navigate(CreatePhotoTask)

                            } else {
                                val task = Task(
                    taskId = taskToEdit?.taskId ?: 0,
                    title = viewModel.currentTaskTitle,
                    description = viewModel.currentTaskDescription,
                    points = viewModel.currentTaskPoints.toIntOrNull() ?: 0,
                    latitude = viewModel.currentLatitude,
                    longitude = viewModel.currentLongitude,
                    markerColor = viewModel.currentMarkerColor,
                    taskType = selectedTaskType,
                    quizDetails = if (selectedTaskType == "Quiz") quizViewModel.getCurrentQuiz() else null,
                    noteDetails = if (selectedTaskType == "Note") noteViewModel.getCurrentNote() else null,
                    trueFalseDetails = if (selectedTaskType == "True/False") trueFalseViewModel.getCurrentTrueFalse() else null,
                    openQuestionDetails = if (selectedTaskType == "Open question") openQuestionViewModel.getCurrentOpenQuestion() else null,
                    photoDetails = if (selectedTaskType == "Photo") photoViewModel.getCurrentPhotoTask() else null
                )
                    viewModel.addOrUpdateTask(task)

                    //task types vms reset
                    quizViewModel.resetQuiz()
                    noteViewModel.resetNote()
                    photoViewModel.resetPhotoTask()
                    trueFalseViewModel.resetTrueFalse()
                    openQuestionViewModel.resetOpenQuestion()

                    viewModel.setTaskDetailsEntered(false)
                    viewModel.setTaskToEdit(null)
                    navController.navigate(Creator)
                }
            },
            enabled = isTaskDetailsEntered || taskToEdit != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isTaskDetailsEntered || taskToEdit != null) button_green else Color.Gray,
                contentColor = Color.White
            ),
            modifier = Modifier.padding(elementSpacing)
            ) {
                Text("Save Task", color = Color.White)
            }
                }
            }
        }
    }

    if (isMapFullScreen) {
        Dialog(
            onDismissRequest = { isMapFullScreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = fullscreenCameraPositionState,
                    onMapClick = { latLng ->
                        latitude = latLng.latitude
                        longitude = latLng.longitude
                        temporaryMarker = latLng
                        updateViewModel()
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
                    onClick = { isMapFullScreen = false },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = button_green)
                ) {
                    Text("Close Full Screen Map", color = Color.White)
                }
            }
        }
    }
}
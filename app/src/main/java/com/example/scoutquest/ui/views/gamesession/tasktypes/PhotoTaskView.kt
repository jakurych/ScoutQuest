package com.example.scoutquest.ui.views.gamesession.tasktypes

import android.content.Context
import androidx.camera.core.Camera
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.scoutquest.data.models.tasktypes.Photo
import com.example.scoutquest.utils.AnswersChecker
import com.example.scoutquest.viewmodels.gamesession.GameSessionViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoTaskView(
    photoTask: Photo,
    viewModel: GameSessionViewModel,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    var showResult by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    val answersChecker = AnswersChecker()
    val coroutineScope = rememberCoroutineScope()
    var capturedImageUri by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Photo Task") })
        },
        content = { paddingValues ->
            if (showResult) {
                viewModel.updateTaskScore(score)
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Task Completed! You scored $score points.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Continue")
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Text(
                        text = photoTask.instruction,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (capturedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(capturedImageUri),
                            contentDescription = "Captured Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Button(
                        onClick = {
                            // Implement image capture logic here
                            captureImage(context) { uri ->
                                capturedImageUri = uri
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Capture Image")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Take Photo")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (capturedImageUri != null) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    score = answersChecker.checkPhoto(capturedImageUri!!, photoTask.instruction, context)
                                    showResult = true
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Submit Photo")
                        }
                    }
                }
            }
        }
    )
}

// Placeholder function for image capture
fun captureImage(context: Context, onImageCaptured: (String) -> Unit) {
    // Implement logic to capture image and get URI
    // In actual implementation, use CameraX or other library to capture image
    // And handle permissions
    // On capturing the image, call onImageCaptured(imageUri)
    onImageCaptured("file://path_to_captured_image") // Placeholder URI
}

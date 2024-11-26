package com.example.scoutquest.ui.views.gamesession.tasktypes

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
import com.example.scoutquest.utils.ComposeFileProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoTaskView(
    photoTask: Photo,
    viewModel: GameSessionViewModel,
    onComplete: (Int) -> Unit // Zmiana typu na () -> Unit
) {
    val context = LocalContext.current
    var showResult by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPreview by remember { mutableStateOf(false) }
    val answersChecker = AnswersChecker()
    val coroutineScope = rememberCoroutineScope()
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            showPreview = true
        } else {
            errorMessage = "Failed to capture image"
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            capturedImageUri = it
            showPreview = true
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                val uri = ComposeFileProvider.getImageUri(context)
                capturedImageUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                errorMessage = "Error creating image file: ${e.message}"
                Log.e("PhotoTaskView", "Error creating image file", e)
            }
        } else {
            errorMessage = "Camera permission is required"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photo Task") }
            )
        }
    ) { paddingValues ->
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
                    onClick = { onComplete(score) }, // Przekazanie score jako argument
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
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Text(
                    text = "Make photo of: ${photoTask.description}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (showPreview && capturedImageUri != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(capturedImageUri),
                            contentDescription = "Captured Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                showPreview = false
                                capturedImageUri = null
                            }
                        ) {
                            Text("Retake Photo")
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        capturedImageUri?.let { uri ->
                                            score = answersChecker.checkPhoto(
                                                uri.toString(),
                                                photoTask.description,
                                                context
                                            )
                                            showResult = true
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Error processing photo: ${e.message}"
                                    }
                                }
                            }
                        ) {
                            Text("Submit Photo")
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Button(
                            onClick = {
                                errorMessage = null
                                permissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Take Photo")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Take Photo")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                errorMessage = null
                                galleryLauncher.launch("image/*")
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Choose from Gallery")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Choose from Gallery (test mod)")
                        }
                    }
                }
            }
        }
    }
}




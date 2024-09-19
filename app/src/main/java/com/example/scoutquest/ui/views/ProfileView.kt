package com.example.scoutquest.ui.views

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scoutquest.data.models.User
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.ui.navigation.MainScreenRoute
import com.example.scoutquest.ui.theme.*
import com.example.scoutquest.viewmodels.ProfileViewModel
import com.example.scoutquest.viewmodels.UserViewModel
import com.example.scoutquest.data.models.Badge
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.navigation.UserBrowser
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ProfileView(profileViewModel: ProfileViewModel, userViewModel: UserViewModel) {
    val navController = LocalNavigation.current
    val isUserLoggedIn by userViewModel.isUserLoggedIn.collectAsState()
    val user by profileViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserData()
    }

    if (isUserLoggedIn == false) {
        LaunchedEffect(Unit) {
            navController.navigate(MainScreenRoute) {
                popUpTo(0) { inclusive = true }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Header()
        Spacer(modifier = Modifier.height(16.dp))

        user?.let {
            ProfileHeader(user = it, profileViewModel = profileViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileDetails(user = it, isEmailVerified = profileViewModel.isEmailVerified())
            Spacer(modifier = Modifier.height(16.dp))
            ProfileActions(it, profileViewModel, navController)
        }
    }
}

@Composable
fun ProfileHeader(user: User, profileViewModel: ProfileViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showPhotoUpdateDialog by remember { mutableStateOf(false) }
    var photoUpdateMessage by remember { mutableStateOf("") }
    var photoUpdateSuccess by remember { mutableStateOf<Boolean?>(null) }
    var showImagePickerDialog by remember { mutableStateOf(false) }

    val imageUri = remember { mutableStateOf<Uri?>(null) }

    //picture with the camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri.value?.let {
                showPhotoUpdateDialog = true
                photoUpdateSuccess = null
                photoUpdateMessage = "Changing photo process in progress. Your photo should be updated soon!"
                handleImageUri(it, context, profileViewModel, scope, onSuccess = {
                    photoUpdateSuccess = true
                    photoUpdateMessage = "Profile picture updated successfully!"
                }, onError = {
                    photoUpdateSuccess = false
                    photoUpdateMessage = "Failed to update profile picture."
                })
            }
        }
    }

    //picture from gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            showPhotoUpdateDialog = true
            photoUpdateSuccess = null
            photoUpdateMessage = "Changing photo process in progress. Your photo should be updated soon!"
            handleImageUri(it, context, profileViewModel, scope, onSuccess = {
                photoUpdateSuccess = true
                photoUpdateMessage = "Profile picture updated successfully!"
            }, onError = {
                photoUpdateSuccess = false
                photoUpdateMessage = "Failed to update profile picture."
            })
        }
    }

    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Select Image Source", color = Color.White) },
            text = { Text("Choose an option to update your profile picture", color = Color.White) },
            confirmButton = {
                TextButton(onClick = {
                    showImagePickerDialog = false
                    val photoFile = createImageFile(context)
                    imageUri.value = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        photoFile
                    )
                    cameraLauncher.launch(imageUri.value)
                }) {
                    Text("Camera", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImagePickerDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Gallery", color = Color.White)
                }
            },
            containerColor = drab_dark_brown
        )
    }

    if (showPhotoUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoUpdateDialog = false },
            confirmButton = {
                TextButton(onClick = { showPhotoUpdateDialog = false }) {
                    Text("OK", color = Color.White)
                }
            },
            title = {
                Text(
                    when (photoUpdateSuccess) {
                        true -> "Success"
                        false -> "Error"
                        null -> "Information"
                    },
                    color = Color.White
                )
            },
            text = { Text(photoUpdateMessage, color = Color.White) },
            containerColor = when (photoUpdateSuccess) {
                true -> moss_green
                false -> Color.Red
                null -> drab_dark_brown
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = drab_dark_brown)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = user.profilePictureUrl ?: "https://via.placeholder.com/150"
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable {
                        showImagePickerDialog = true
                    },
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}



private fun handleImageUri(
    uri: Uri,
    context: Context,
    profileViewModel: ProfileViewModel,
    scope: CoroutineScope,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    scope.launch {
        profileViewModel.uploadProfilePicture(
            uri,
            context,
            onSuccess = {
                profileViewModel.fetchUserData()
                onSuccess()
            },
            onError = {
                onError()
            }
        )
    }
}


private fun createImageFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${System.currentTimeMillis()}_",
        ".jpg",
        storageDir
    )
}

@Composable
fun ProfileDetails(user: User, isEmailVerified: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = black_olive)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Email: ${user.email ?: "No email"}", color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            BadgesRow(user.badges)
            Spacer(modifier = Modifier.height(8.dp))

            if (isEmailVerified) {
                Text("Points: ${user.points}", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Created Games: ${user.createdGames?.size ?: 0}", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Games History: ${user.gamesHistory?.size ?: 0}", color = Color.White)
            } else {
                Text("Please verify your email to get stats", color = Color.White)
            }
        }
    }
}

@Composable
fun BadgesRow(badges: List<Badge>?) {
    Row {
        badges?.forEach { badge ->
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(moss_green, CircleShape)
                    .padding(4.dp)
            ) {
                Text(
                    text = badge.name.take(1),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun ProfileActions(user: User, profileViewModel: ProfileViewModel, navController: NavController) {
    var showEmailDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val userHasGames = user.createdGames?.isNotEmpty() == true

    if (showEmailDialog) {
        ChangeEmailDialog(
            onDismiss = { showEmailDialog = false },
            onConfirm = { newEmail, currentPassword ->
                profileViewModel.updateEmail(
                    newEmail = newEmail,
                    password = currentPassword,
                    onSuccess = {
                        successMessage = "Verification mail sent to new email address"
                        showSuccessDialog = true
                        showEmailDialog = false
                    },
                    onError = { error ->
                        errorMessage = error
                        showErrorDialog = true
                    }
                )
            }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { newPassword, currentPassword ->
                profileViewModel.updatePassword(
                    newPassword = newPassword,
                    password = currentPassword,
                    onSuccess = {
                        successMessage = "Password changed successfully"
                        showSuccessDialog = true
                        showPasswordDialog = false
                    },
                    onError = { error ->
                        errorMessage = error
                        showErrorDialog = true
                    }
                )
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("OK", color = Color.White)
                }
            },
            title = { Text("Success", color = Color.White) },
            text = { Text(successMessage, color = Color.White) },
            containerColor = button_green
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK", color = Color.White)
                }
            },
            title = { Text("Error", color = Color.White) },
            text = { Text(errorMessage, color = Color.White) },
            containerColor = drab_dark_brown
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = eerie_black)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            if (userHasGames) {
                Button(
                    onClick = {
                        navController.navigate(UserBrowser)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = button_green),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("My Games", color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = { showEmailDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = button_green),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Email", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { showPasswordDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = button_green),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Password", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    profileViewModel.logout()
                    navController.navigate(MainScreenRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = button_green),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEmailDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var currentPassword by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (newEmail == confirmEmail) {
                    onConfirm(newEmail, currentPassword)
                } else {
                    errorMessage = "Emails do not match"
                }
            }) {
                Text("Confirm", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        title = { Text("Change Email", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("New Email") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.White
                    )
                )
                OutlinedTextField(
                    value = confirmEmail,
                    onValueChange = { confirmEmail = it },
                    label = { Text("Confirm Email") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.White
                    )
                )
                if (errorMessage != null) {
                    Text(errorMessage!!, color = Color.Red)
                }
            }
        },
        containerColor = drab_dark_brown
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                when {
                    newPassword == currentPassword -> {
                        errorMessage = "New and old passwords are the same!"
                    }
                    newPassword != confirmPassword -> {
                        errorMessage = "Passwords do not match"
                    }
                    else -> {
                        onConfirm(newPassword, currentPassword)
                        errorMessage = null
                    }
                }
            }) {
                Text("Confirm", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        title = { Text("Change Password", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )
                if (errorMessage != null) {
                    Text(errorMessage!!, color = Color.Red)
                }
            }
        },
        containerColor = drab_dark_brown
    )
}
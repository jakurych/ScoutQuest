package com.example.scoutquest.ui.views

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
import coil.compose.rememberImagePainter
import com.example.scoutquest.data.models.User
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.ui.navigation.MainScreenRoute
import com.example.scoutquest.ui.theme.*
import com.example.scoutquest.viewmodels.ProfileViewModel
import com.example.scoutquest.viewmodels.UserViewModel
import com.example.scoutquest.data.models.Badge

@Composable
fun ProfileView(profileViewModel: ProfileViewModel, userViewModel: UserViewModel) {
    val navController = LocalNavigation.current
    val isUserLoggedIn by userViewModel.isUserLoggedIn.collectAsState()
    val user by profileViewModel.user.collectAsState()

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
        user?.let {
            ProfileHeader(user = it)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileDetails(user = it)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileActions(profileViewModel, navController)
        }
    }
}

@Composable
fun ProfileHeader(user: User) {
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
                painter = rememberImagePainter(data = user.profilePictureUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
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

@Composable
fun ProfileDetails(user: User) {
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
            Text("Points: ${user.points}", color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Created Games: ${user.createdGames?.size ?: 0}", color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Games History: ${user.gamesHistory?.size ?: 0}", color = Color.White)
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
                    text = badge.name.take(1), // First letter of badge as placeholder
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
fun ProfileActions(profileViewModel: ProfileViewModel, navController: NavController) {
    var showEmailDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    if (showEmailDialog) {
        ChangeEmailDialog(
            onDismiss = { showEmailDialog = false },
            onConfirm = { newEmail ->
                profileViewModel.updateEmail(newEmail)
                showEmailDialog = false
            }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { newPassword ->
                profileViewModel.updatePassword(newPassword)
                showPasswordDialog = false
            }
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
fun ChangeEmailDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var newEmail by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (newEmail == confirmEmail) {
                    onConfirm(newEmail)
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
fun ChangePasswordDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (newPassword == confirmPassword) {
                    onConfirm(newPassword)
                } else {
                    errorMessage = "Passwords do not match"
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



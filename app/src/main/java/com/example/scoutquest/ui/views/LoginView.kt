package com.example.scoutquest.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.ui.navigation.MainScreenRoute
import com.example.scoutquest.ui.navigation.Register
import com.example.scoutquest.viewmodels.LoginViewModel
import com.example.scoutquest.viewmodels.UserViewModel
import com.example.scoutquest.ui.theme.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

@Composable
fun LoginView(loginViewModel: LoginViewModel, userViewModel: UserViewModel) {
    val navController = LocalNavigation.current
    val isUserLoggedIn by userViewModel.isUserLoggedIn.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()

    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetErrorMessage by remember { mutableStateOf("") }

    LaunchedEffect(isUserLoggedIn) {
        if (isUserLoggedIn == true) {
            navController.navigate(MainScreenRoute) {
                popUpTo(0)
            }
        }
    }

    Column {
        Header()
        TextField(
            value = usernameOrEmail,
            onValueChange = { usernameOrEmail = it },
            label = { Text("Username or Email") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                loginViewModel.login(usernameOrEmail, password)
                userViewModel.checkLoginState()
            },
            colors = ButtonDefaults.buttonColors(containerColor = button_green),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Button(
            onClick = { navController.navigate(Register) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = moss_green)
        ) {
            Text("Go to Register")
        }
        Button(
            onClick = { navController.navigate(MainScreenRoute) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Back to Home Screen")
        }
        Button(
            onClick = { showResetPasswordDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = drab_dark_brown)
        ) {
            Text("Forgot Password?")
        }

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }

        if (showResetPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showResetPasswordDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        userViewModel.resetPassword(resetEmail) { success ->
                            if (success) {
                                showResetPasswordDialog = false
                            } else {
                                resetErrorMessage = "Email not found"
                            }
                        }
                    }) {
                        Text("Send", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetPasswordDialog = false }) {
                        Text("Cancel", color = Color.White)
                    }
                },
                title = { Text("Reset Password", color = Color.White) },
                text = {
                    Column {
                        TextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Enter your email") },
                            modifier = Modifier.fillMaxWidth()

                        )
                        if (resetErrorMessage.isNotEmpty()) {
                            Text(text = resetErrorMessage, color = Color.Red)
                        }
                    }
                },
                containerColor = black_olive
            )
        }
    }
}


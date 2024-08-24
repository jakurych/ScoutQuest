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

@Composable
fun LoginView(loginViewModel: LoginViewModel, userViewModel: UserViewModel) {
    val navController = LocalNavigation.current
    val isUserLoggedIn by userViewModel.isUserLoggedIn.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()

    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }
    }
}

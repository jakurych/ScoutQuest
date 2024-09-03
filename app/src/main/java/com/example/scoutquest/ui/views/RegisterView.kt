import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.navigation.Login
import com.example.scoutquest.ui.navigation.Register
import com.example.scoutquest.ui.theme.button_green
import com.example.scoutquest.ui.theme.moss_green
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.ui.navigation.MainScreenRoute

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun RegisterView(registerViewModel: RegisterViewModel = viewModel()) {
    val navController = LocalNavigation.current
    var showDialog by remember { mutableStateOf(false) }

    Column {
        Header()
        TextField(
            value = registerViewModel.username,
            onValueChange = { registerViewModel.username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = registerViewModel.email,
            onValueChange = { registerViewModel.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = registerViewModel.password,
            onValueChange = { registerViewModel.password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        TextField(
            value = registerViewModel.confirmPassword,
            onValueChange = { registerViewModel.confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = { registerViewModel.register() },
            colors = ButtonDefaults.buttonColors(containerColor = button_green),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        Button(
            onClick = { navController.navigate(Login) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = moss_green)
        ) {
            Text("Go to Login")
        }
        Button(
            onClick = { navController.navigate(MainScreenRoute) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Back to Home Screen")
        }

        if (registerViewModel.errorMessage.isNotEmpty()) {
            Text(text = registerViewModel.errorMessage, color = Color.Red)
        }

        LaunchedEffect(registerViewModel.registrationSuccess) {
            if (registerViewModel.registrationSuccess) {
                showDialog = true
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        navController.navigate(Login)
                    }) {
                        Text("OK", color = Color.White)
                    }
                },
                title = { Text("Registration Successful", color = Color.White) },
                text = { Text("Verification email sent to your address.", color = Color.White) },
                containerColor = button_green
            )
        }
    }
}



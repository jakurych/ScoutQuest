import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.scoutquest.ui.components.Header
import com.example.scoutquest.ui.navigation.LocalNavigation
import com.example.scoutquest.ui.navigation.Profile
import com.example.scoutquest.ui.navigation.Register
import com.example.scoutquest.viewmodels.LoginViewModel
import com.example.scoutquest.ui.theme.*

@Composable
fun LoginView(loginViewModel: LoginViewModel) {
    val navController = LocalNavigation.current

    Column {
        Header()
        TextField(
            value = loginViewModel.username,
            onValueChange = { loginViewModel.username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = loginViewModel.password,
            onValueChange = { loginViewModel.password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = { loginViewModel.login() },
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

        if (loginViewModel.errorMessage.isNotEmpty()) {
            Text(text = loginViewModel.errorMessage, color = Color.Red)
        }

        //po pomyślnym zalogowaniu
        LaunchedEffect(loginViewModel.loginSuccess) {
            if (loginViewModel.loginSuccess) {
                navController.navigate(Profile)
            }
        }
    }
}

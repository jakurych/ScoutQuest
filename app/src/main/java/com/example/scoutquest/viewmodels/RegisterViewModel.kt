import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.User
import com.example.scoutquest.data.repositories.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var registrationSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    private val userRepository = UserRepository()

    fun register() {
        viewModelScope.launch {
            // Sprawdź, czy użytkownik o podanym adresie e-mail już istnieje
            val existingUser = userRepository.getUserByEmail(email)
            if (existingUser != null) {
                errorMessage = "Email already in use"
                registrationSuccess = false
                return@launch
            }

            // Utwórz nowego użytkownika
            val newUser = User(username = username, email = email, password = password)
            val success = userRepository.addUser(newUser)
            if (success) {
                registrationSuccess = true
                errorMessage = ""
            } else {
                registrationSuccess = false
                errorMessage = "Registration failed"
            }
        }
    }
}

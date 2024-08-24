import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.User
import com.example.scoutquest.data.repositories.UserRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var registrationSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun register() {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Pobierz UID nowo utworzonego użytkownika
                        val userId = task.result?.user?.uid ?: ""

                        // Dodaj użytkownika do Firestore
                        val db = FirebaseFirestore.getInstance()
                        val user = User(username = username, email = email, userId = userId)
                        db.collection("users").document(userId).set(user).addOnSuccessListener {
                            registrationSuccess = true
                            errorMessage = ""
                        }.addOnFailureListener { e ->
                            registrationSuccess = false
                            errorMessage = e.message ?: "Failed to add user to Firestore"
                        }
                    } else {
                        registrationSuccess = false
                        errorMessage = task.exception?.message ?: "Registration failed"
                    }
                }
            } catch (e: Exception) {
                registrationSuccess = false
                errorMessage = e.message ?: "Registration failed"
            }
        }
    }

}

package com.example.scoutquest.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.repositories.UserRepository
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var usernameOrEmail by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    fun login() {
        viewModelScope.launch {
            if (usernameOrEmail.contains("@")) {
                // Logowanie przez email
                loginWithEmail(usernameOrEmail, password)
            } else {
                // Logowanie przez username
                loginWithUsername(usernameOrEmail, password)
            }
        }
    }

    private suspend fun loginWithEmail(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Logowanie się powiodło
                        errorMessage = ""
                    } else {
                        // Logowanie się nie powiodło
                        errorMessage = "Invalid email or password"
                    }
                }
        } catch (e: Exception) {
            errorMessage = "Login error: ${e.message}"
        }
    }

    private suspend fun loginWithUsername(username: String, password: String) {
        val user = userRepository.getUserByUsername(username)
        if (user == null) {
            errorMessage = "Invalid username"
            return
        }

        val email = user.email ?: run {
            errorMessage = "Email not found for user"
            return
        }

        loginWithEmail(email, password)
    }
}

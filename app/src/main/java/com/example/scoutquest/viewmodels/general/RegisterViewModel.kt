package com.example.scoutquest.viewmodels.general

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import android.util.Log
import com.example.scoutquest.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("") // Dodano pole potwierdzenia hasła
    var registrationSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun register() {
        if (password != confirmPassword) {
            errorMessage = "Passwords do not match"
            return
        }

        viewModelScope.launch {
            try {
                authRepository.registerUser(username, email, password)
                registrationSuccess = true
                errorMessage = ""
            } catch (e: Exception) {
                registrationSuccess = false
                errorMessage = e.message ?: "Registration failed"
            }
        }
    }

}


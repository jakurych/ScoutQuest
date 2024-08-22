package com.example.scoutquest.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.repositories.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var loginSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    private val userRepository = UserRepository()

    fun login() {
        viewModelScope.launch {
            val user = userRepository.getUserByUsername(username)
            if (user != null) {
                println("Found user: ${user.username}")
                if (user.password == password) {
                    loginSuccess = true
                    errorMessage = ""
                } else {
                    println("Password mismatch")
                    loginSuccess = false
                    errorMessage = "Invalid username or password"
                }
            } else {
                println("User not found")
                loginSuccess = false
                errorMessage = "Invalid username or password"
            }
        }
    }

}

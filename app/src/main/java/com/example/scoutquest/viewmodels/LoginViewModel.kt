package com.example.scoutquest.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.repositories.AuthRepository
import com.example.scoutquest.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                loginWithEmail(email, password)
            } catch (e: Exception) {
                _errorMessage.value = "Login error: ${e.message}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            _isUserLoggedIn.value = false
        }
    }

    private suspend fun loginWithEmail(email: String, password: String) {
        try {
            authRepository.loginWithEmail(email, password)
            _errorMessage.value = ""
            _isUserLoggedIn.value = true
            authRepository.checkLoginState()
        } catch (e: Exception) {
            _errorMessage.value = "Invalid email or password"
        }
    }

    fun clearError() {
        _errorMessage.value = ""
    }
}

package com.example.scoutquest.viewmodels.general

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            try {
                authRepository.loginWithEmailOrUsername(identifier, password)
                _errorMessage.value = ""
                _isUserLoggedIn.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error occurred"
                _isUserLoggedIn.value = false
            }
        }
    }




    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            _isUserLoggedIn.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = ""
    }
}

package com.example.scoutquest.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isUserLoggedIn = MutableStateFlow<Boolean?>(null)
    val isUserLoggedIn: StateFlow<Boolean?> = _isUserLoggedIn

    init {
        viewModelScope.launch {
            authRepository.isUserLoggedIn.collect { isLoggedIn ->
                _isUserLoggedIn.value = isLoggedIn
            }
        }
    }

    fun checkLoginState() {
        authRepository.checkLoginState()
    }

    fun logout() {
        viewModelScope.launch {
            _isUserLoggedIn.value = false
            authRepository.signOut()
        }
    }

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            try {
                authRepository.loginWithEmailOrUsername(identifier, password)
            } catch (e: Exception) {
            }
        }
    }
}


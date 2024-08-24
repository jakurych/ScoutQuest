package com.example.scoutquest.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val isUserLoggedIn = authRepository.isUserLoggedIn
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail

    init {
        checkUserLoggedIn()
    }

    fun checkUserLoggedIn() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _userEmail.value = currentUser?.email
        }
    }

    fun logout() {
        authRepository.signOut()
        checkUserLoggedIn()
    }
}

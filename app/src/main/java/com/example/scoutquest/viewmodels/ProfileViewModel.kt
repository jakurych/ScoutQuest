package com.example.scoutquest.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.User
import com.example.scoutquest.data.repositories.AuthRepository
import com.example.scoutquest.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _userEmail = MutableStateFlow<String?>(null)

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        viewModelScope.launch {
            authRepository.isUserLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    fetchUserData()
                } else {
                    _user.value = null
                }
            }
        }
    }

    fun fetchUserData() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            currentUser?.let {
                _user.value = userRepository.getUserById(it.uid)
                _userEmail.value = it.email
            }
        }
    }

    fun refreshUserData() {
        fetchUserData()
    }

    fun isEmailVerified(): Boolean {
        return authRepository.getCurrentUser()?.isEmailVerified ?: false
    }

    fun logout() {
        authRepository.signOut()
    }

    fun updateEmail(newEmail: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.changeEmail(newEmail, password)
                _userEmail.value = newEmail
                authRepository.sendEmailVerification()
                onSuccess()
                refreshUserData()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to update email")
            }
        }
    }

    fun updatePassword(newPassword: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val success = authRepository.changePassword(newPassword, password)
                if (success) {
                    onSuccess()
                    refreshUserData()
                }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to update password")
            }
        }
    }
}


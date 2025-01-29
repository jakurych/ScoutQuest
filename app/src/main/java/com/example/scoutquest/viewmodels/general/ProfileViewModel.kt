package com.example.scoutquest.viewmodels.general

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.User
import com.example.scoutquest.data.repositories.AuthRepository
import com.example.scoutquest.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _userEmail = MutableStateFlow<String?>(null)

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified

    private val _isCheckingVerification = MutableStateFlow(false)
    val isCheckingVerification: StateFlow<Boolean> = _isCheckingVerification

    init {
        viewModelScope.launch {
            authRepository.isUserLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    fetchUserData()
                    startEmailVerificationCheck()
                } else {
                    _user.value = null
                }
            }
        }
    }

    private fun startEmailVerificationCheck() {
        viewModelScope.launch {
            while (true) {
                checkEmailVerification()
                delay(5000) //sprawdza co 5 sekund
            }
        }
    }

    private suspend fun checkEmailVerification() {
        try {
            _isCheckingVerification.value = true
            authRepository.getCurrentUser()?.let { user ->
                authRepository.reloadCurrentUser()?.let { freshUser ->
                    _isEmailVerified.value = freshUser.isEmailVerified
                    if (freshUser.isEmailVerified) {
                        fetchUserData()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error checking email verification: ${e.message}")
        } finally {
            _isCheckingVerification.value = false
        }
    }


    fun isEmailVerified(): Boolean {
        return _isEmailVerified.value
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

    private fun refreshUserData() {
        fetchUserData()
    }

    fun logout() {
        authRepository.signOut()
    }

    fun updateEmail(newEmail: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.changeEmail(newEmail, password)
                _userEmail.value = newEmail
                onSuccess()
                refreshUserData()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating email: ${e.message}")
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

    fun uploadProfilePicture(
        uri: Uri,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.uid
            userId?.let {
                val imageUrl = userRepository.uploadProfileImage(it, uri, context)
                if (imageUrl != null) {
                    fetchUserData()
                    onSuccess()
                } else {
                    onError("Failed to upload image")
                }
            }
        }
    }



}




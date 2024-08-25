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
    val isUserLoggedIn = authRepository.isUserLoggedIn
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail

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

    private fun fetchUserData() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            currentUser?.let {
                _user.value = userRepository.getUserById(it.uid)
            }
        }
    }

    fun logout() {
        authRepository.signOut()
    }
}


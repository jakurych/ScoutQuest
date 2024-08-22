package com.example.scoutquest.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> get() = _isUserLoggedIn

    fun checkUserLoggedIn() {
        viewModelScope.launch {
            _isUserLoggedIn.value = false
        }
    }
}

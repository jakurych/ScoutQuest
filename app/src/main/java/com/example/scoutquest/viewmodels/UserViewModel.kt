package com.example.scoutquest.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _isUserLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isUserLoggedIn: StateFlow<Boolean> get() = _isUserLoggedIn

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val isLoggedIn = firebaseAuth.currentUser != null
            println("Auth state changed: isLoggedIn = $isLoggedIn")
            _isUserLoggedIn.value = isLoggedIn
        }
    }


    fun signOut() {
        auth.signOut()
    }
}

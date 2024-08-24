package com.example.scoutquest.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val userRepository: UserRepository
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _isUserLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _isUserLoggedIn.value = firebaseAuth.currentUser != null
        }
    }

    fun checkLoginState() {
        _isUserLoggedIn.value = auth.currentUser != null
    }

    fun signOut() {
        auth.signOut()
        _isUserLoggedIn.value = false
    }

    suspend fun loginWithEmailOrUsername(identifier: String, password: String) {
        val email = if (identifier.contains("@")) {
            identifier
        } else {
            userRepository.getEmailByUsername(identifier)
        }

        if (email != null) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isUserLoggedIn.value = true
                } else {
                    throw task.exception ?: Exception("Login failed")
                }
            }
        } else {
            throw Exception("Invalid username or email")
        }
    }
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}

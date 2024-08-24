package com.example.scoutquest.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
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
        try {
            val email = if (identifier.contains("@")) {
                identifier
            } else {
                userRepository.getEmailByUsername(identifier)
            }

            if (email != null) {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                if (result.user != null) {
                    _isUserLoggedIn.value = true
                } else {
                    throw Exception("Login failed: User is null")
                }
            } else {
                throw Exception("Invalid username or email")
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw Exception("Invalid credentials: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Login error: ${e.message}")
        }
    }


    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}

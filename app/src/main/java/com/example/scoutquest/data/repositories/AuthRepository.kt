package com.example.scoutquest.data.repositories

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
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

    private suspend fun reauthenticateUser(password: String) {
        val user = auth.currentUser
        user?.let {
            val credential = EmailAuthProvider.getCredential(it.email!!, password)
            try {
                it.reauthenticate(credential).await()
            } catch (e: Exception) {
                throw Exception("Reauthentication failed: ${e.message}")
            }
        }
    }

    suspend fun changeEmail(newEmail: String, password: String) {
        val user = auth.currentUser
        user?.let {
            try {
                reauthenticateUser(password)
                it.updateEmail(newEmail).await()
                userRepository.updateUserEmail(it.uid, newEmail)
            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                throw Exception("Reauthentication required: ${e.message}")
            } catch (e: Exception) {
                throw Exception("Failed to update email: ${e.message}")
            }
        }
    }

    suspend fun changePassword(newPassword: String, password: String): Boolean {
        val user = auth.currentUser
        return try {
            reauthenticateUser(password)
            user?.updatePassword(newPassword)?.await()
            true
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            throw Exception("Reauthentication required: ${e.message}")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw Exception("Invalid current password: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Failed to update password: ${e.message}")
        }
    }
}

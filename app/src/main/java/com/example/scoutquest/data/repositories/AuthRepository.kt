package com.example.scoutquest.data.repositories

import android.util.Log
import com.example.scoutquest.data.models.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
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
        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            try {
                // Reauthenticate user
                user.reauthenticate(credential).await()

                // Update email
                user.updateEmail(newEmail).await()

                // Send verification email
                user.sendEmailVerification().await()

                // Update email in Firestore
                userRepository.updateUserEmail(user.uid, newEmail)

                Log.d("AuthRepository", "Email updated and verification sent successfully")
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error updating email: ${e.message}")
                throw e
            }
        } else {
            throw Exception("User is not logged in")
        }
    }

    suspend fun reloadCurrentUser(): FirebaseUser? {
        return try {
            auth.currentUser?.reload()?.await()
            auth.currentUser
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error reloading user: ${e.message}")
            null
        }
    }



    suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            val signInMethodsResult = auth.fetchSignInMethodsForEmail(email).await()
            val signInMethods = signInMethodsResult.signInMethods

            if (!signInMethods.isNullOrEmpty()) {
                auth.sendPasswordResetEmail(email).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to send password reset email: ${e.message}")
            false
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

    suspend fun sendEmailVerification() {
        val user = auth.currentUser
        user?.let {
            try {
                it.sendEmailVerification().await()
                Log.d("AuthRepository", "Verification email sent.")
            } catch (e: Exception) {
                Log.e("AuthRepository", "Failed to send verification email: ${e.message}")
                throw Exception("Failed to send verification email: ${e.message}")
            }
        }
    }

    suspend fun registerUser(username: String, email: String, password: String): String {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")

            // Dodaj użytkownika do Firestore
            val user = User(username = username, email = email, userId = userId)
            userRepository.addUser(user)

            // Wyślij e-mail weryfikacyjny
            sendEmailVerification()

            return userId
        } catch (e: FirebaseAuthUserCollisionException) {
            throw Exception("User already exists: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Registration failed: ${e.message}")
        }
    }




}

package com.example.scoutquest.data.repositories

import com.example.scoutquest.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getEmailByUsername(username: String): String? {
        return try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()

            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].getString("email")
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error fetching email by username: ${e.message}")
            null
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val documentSnapshot = db.collection("users")
                .document(userId)
                .get()
                .await()

            documentSnapshot.toObject(User::class.java)
        } catch (e: Exception) {
            println("Error fetching user by ID: ${e.message}")
            null
        }
    }

    suspend fun getUserIdByEmail(email: String): String? {
        return try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].id
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error fetching user ID by email: ${e.message}")
            null
        }
    }

    suspend fun updateUserEmail(userId: String, newEmail: String) {
        try {
            val userRef = db.collection("users").document(userId)
            userRef.update("email", newEmail).await()
        } catch (e: Exception) {
            println("Error updating user email: ${e.message}")
        }
    }

    suspend fun removeGameFromUser(userId: String, gameId: String) {
        try {
            val userRef = db.collection("users").document(userId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val createdGames = snapshot.get("createdGames") as? List<String> ?: emptyList()
                val updatedGames = createdGames.filter { it != gameId }
                transaction.update(userRef, "createdGames", updatedGames)
            }.await()
        } catch (e: Exception) {
            println("Error removing game from user: ${e.message}")
        }
    }

}

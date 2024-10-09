package com.example.scoutquest.data.repositories

import android.content.Context
import android.net.Uri
import com.example.scoutquest.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.storage.FirebaseStorage

class UserRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference
    private val auth = FirebaseAuth.getInstance()


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

    fun getUserId(): String? {
        return auth.currentUser?.uid
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

    suspend fun updateUserPoints(userId: String, pointsToAdd: Int) {
        try {
            val userRef = db.collection("users").document(userId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentPoints = snapshot.getLong("points")?.toInt() ?: 0
                val newPoints = currentPoints + pointsToAdd
                transaction.update(userRef, "points", newPoints)
            }.await()
        } catch (e: Exception) {
            println("Error updating user points: ${e.message}")
        }
    }

    suspend fun addGameToUserHistory(userId: String, sessionId: String) {
        try {
            val userRef = db.collection("users").document(userId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val gamesHistory = snapshot.get("gamesHistory") as? List<String> ?: emptyList()
                val updatedHistory = gamesHistory + sessionId
                transaction.update(userRef, "gamesHistory", updatedHistory)
            }.await()
        } catch (e: Exception) {
            println("Error adding game to user history: ${e.message}")
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

    suspend fun uploadProfileImage(userId: String, uri: Uri, context: Context): String? {
        return try {
            val profileImagesRef = storageRef.child("profileImages/$userId.jpg")
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.let { stream ->
                val uploadTask = profileImagesRef.putStream(stream)
                uploadTask.await()
                val downloadUrl = profileImagesRef.downloadUrl.await()
                val imageUrl = downloadUrl.toString()
                updateUserProfilePicture(userId, imageUrl)
                imageUrl
            }
        } catch (e: Exception) {
            println("Error uploading profile image: ${e.message}")
            e.printStackTrace()
            null
        }
    }


    private suspend fun updateUserProfilePicture(userId: String, imageUrl: String) {
        try {
            val userRef = db.collection("users").document(userId)
            userRef.update("profilePictureUrl", imageUrl).await()
        } catch (e: Exception) {
            println("Error updating user profile picture: ${e.message}")
        }
    }
}

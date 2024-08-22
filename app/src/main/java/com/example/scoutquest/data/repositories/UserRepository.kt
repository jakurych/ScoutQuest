package com.example.scoutquest.data.repositories

import com.example.scoutquest.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getUserByUsername(username: String): User? {
        return try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()

            println("Query documents count: ${querySnapshot.documents.size}")

            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error fetching user: ${e.message}")
            null
        }
    }



    suspend fun addUser(user: User): Boolean {
        return try {
            val documentReference = db.collection("users").add(user).await()
            val userId = documentReference.id

            // Opcjonalnie: zaktualizuj obiekt User o userId
            db.collection("users").document(userId).update("userId", userId).await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }



}

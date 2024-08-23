package com.example.scoutquest.data.repositories

import com.example.scoutquest.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()

    // Pobierz użytkownika na podstawie nazwy użytkownika
    suspend fun getUserByUsername(username: String): User? {
        return try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()

            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error fetching user by username: ${e.message}")
            null
        }
    }

    // Pobierz użytkownika przez maila
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
            println("Error fetching user by email: ${e.message}")
            null
        }
    }

    // Dodaj nowego użytkownika
    suspend fun addUser(user: User): Boolean {
        return try {
            val documentReference = db.collection("users").add(user).await()
            val userId = documentReference.id

            //zaktualizuj obiekt User o userId
            db.collection("users").document(userId).update("userId", userId).await()

            true
        } catch (e: Exception) {
            println("Error adding user: ${e.message}")
            false
        }
    }
}

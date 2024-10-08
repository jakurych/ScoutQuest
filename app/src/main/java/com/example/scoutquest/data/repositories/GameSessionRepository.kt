package com.example.scoutquest.data.repositories

import com.example.scoutquest.data.models.GameSession
import com.example.scoutquest.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GameSessionRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val gameSessionsCollection = firestore.collection("game_sessions")

    suspend fun createGameSession(gameSession: GameSession) {
        gameSessionsCollection.document(gameSession.sessionId).set(gameSession).await()
    }

    suspend fun getGameSessionById(sessionId: String): GameSession? {
        return try {
            val documentSnapshot = firestore.collection("gameSessions")
                .document(sessionId)
                .get()
                .await()

            documentSnapshot.toObject(GameSession::class.java)
        } catch (e: Exception) {
            println("Error fetching game session by ID: ${e.message}")
            null
        }
    }

    suspend fun updateGameSession(sessionId: String, updatedSession: GameSession) {
        try {
            firestore.collection("gameSessions").document(sessionId)
                .set(updatedSession)
                .await()
        } catch (e: Exception) {
            println("Error updating game session: ${e.message}")
        }
    }

    suspend fun addParticipantToGameSession(sessionId: String, user: User) {
        try {
            val sessionRef = firestore.collection("gameSessions").document(sessionId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(sessionRef)
                val currentParticipants = snapshot.get("participants") as? List<User> ?: listOf()
                val updatedParticipants = currentParticipants + user
                transaction.update(sessionRef, "participants", updatedParticipants)
            }.await()
        } catch (e: Exception) {
            println("Error adding participant to game session: ${e.message}")
        }
    }

    suspend fun deleteGameSession(sessionId: String) {
        gameSessionsCollection.document(sessionId).delete().await()
    }
}


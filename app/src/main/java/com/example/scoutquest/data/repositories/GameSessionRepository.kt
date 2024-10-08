package com.example.scoutquest.data.repositories

import com.example.scoutquest.data.models.GameSession
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
        return gameSessionsCollection.document(sessionId).get().await().toObject(GameSession::class.java)
    }

    suspend fun updateGameSession(sessionId: String, gameSession: GameSession) {
        gameSessionsCollection.document(sessionId).set(gameSession).await()
    }

    suspend fun deleteGameSession(sessionId: String) {
        gameSessionsCollection.document(sessionId).delete().await()
    }
}


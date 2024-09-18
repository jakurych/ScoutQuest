package com.example.scoutquest.data.repositories

import com.example.scoutquest.data.models.Game
import com.example.scoutquest.data.models.Comment
import com.example.scoutquest.data.models.Rating
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GameRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val gamesCollection = firestore.collection("games")

    suspend fun addGame(game: Game): String {
        val documentReference = gamesCollection.add(game).await()
        return documentReference.id //identyfikator nowo utworzonej gry aby utworzyć referencję
    }


    suspend fun removeGame(gameId: String) {
        gamesCollection.document(gameId).delete().await()
    }

    suspend fun getGameById(gameId: String): Game? {
        return gamesCollection.document(gameId).get().await().toObject(Game::class.java)
    }

    suspend fun getGamesByUserId(userId: String): List<Game> {
        return try {
            val querySnapshot = gamesCollection
                .whereEqualTo("creatorId", userId)
                .get()
                .await()
            querySnapshot.toObjects(Game::class.java)
        } catch (e: Exception) {
            println("Error fetching games by user ID: ${e.message}")
            emptyList()
        }
    }


    suspend fun getAllGames(): List<Game> {
        return gamesCollection.get().await().toObjects(Game::class.java)
    }

    suspend fun addCommentToGame(gameId: String, userId: String, userName: String, commentText: String) {
        val game = getGameById(gameId) ?: return
        val newComment = Comment(userId, userName, commentText)
        val updatedRating = (game.rating ?: Rating()).addComment(userId, newComment)
        val updatedGame = game.copy(rating = updatedRating)
        updateGame(gameId, updatedGame)
    }

    suspend fun removeCommentFromGame(gameId: String, userId: String) {
        val game = getGameById(gameId) ?: return
        val updatedRating = game.rating?.removeComment(userId) ?: return
        val updatedGame = game.copy(rating = updatedRating)
        updateGame(gameId, updatedGame)
    }

    suspend fun updateCommentInGame(gameId: String, userId: String, newCommentText: String) {
        val game = getGameById(gameId) ?: return
        val updatedRating = game.rating?.updateComment(userId, newCommentText) ?: return
        val updatedGame = game.copy(rating = updatedRating)
        updateGame(gameId, updatedGame)
    }

    private suspend fun updateGame(gameId: String, game: Game) {
        gamesCollection.document(gameId).set(game).await()
    }
}

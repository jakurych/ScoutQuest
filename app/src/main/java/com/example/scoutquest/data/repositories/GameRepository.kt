package com.example.scoutquest.data.repositories

import com.example.scoutquest.data.models.Game
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class GameRepository {
    private val games = mutableListOf<Game>()



    fun addGame(game: Game) {
        games.add(game)
    }

    fun removeGame(gameId: Int) {
        games.removeAll { it.gameId == gameId }
    }

    fun getGameById(gameId: Int): Game? {
        return games.find { it.gameId == gameId }
    }

    fun getAllGames(): List<Game> {
        return games
    }
}
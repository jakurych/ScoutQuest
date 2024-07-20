package com.example.scoutquest.data.repositories

import com.example.scoutquest.data.models.Game

class GameRepository {
    private val games = mutableListOf<Game>()

    fun addGame(game: Game) {
        games.add(game)
    }

    fun removeGame(gameId: String) {
        games.removeAll { it.gameId == gameId }
    }

    fun getGameById(gameId: String): Game? {
        return games.find { it.gameId == gameId }
    }

    fun getAllGames(): List<Game> {
        return games
    }
}
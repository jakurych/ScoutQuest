package com.example.scoutquest.viewmodels.gamesession

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.data.models.GameSession
import com.example.scoutquest.data.models.Task
import java.util.UUID

class GameSessionViewModel(game: Game) : ViewModel() {
    private val _gameSession = MutableLiveData<GameSession>()
    val gameSession: LiveData<GameSession> = _gameSession

    private val tasks: List<Task> = game.tasks

    init {
        startGameSession(game)
    }

    private fun startGameSession(game: Game) {
        _gameSession.value = GameSession(
            sessionId = UUID.randomUUID().toString(),
            gameId = game.gameId
        )
    }

    fun onTaskReached(task: Task) {
        //osiągnięcie lokalizacji zadania
    }

    fun getTasks(): List<Task> {
        return tasks
    }
}

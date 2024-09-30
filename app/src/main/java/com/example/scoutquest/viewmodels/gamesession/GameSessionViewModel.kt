package com.example.scoutquest.viewmodels.gamesession

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.data.models.GameSession
import com.example.scoutquest.data.models.Task
import java.util.UUID

class GameSessionViewModel : ViewModel() {
    private val _gameSession = MutableLiveData<GameSession>()
    val gameSession: LiveData<GameSession> = _gameSession

    private var tasks: List<Task> = emptyList()
    private var currentTaskIndex by mutableStateOf(0)

    var gameEnded by mutableStateOf(false)
        private set

    fun setGame(game: Game) {
        tasks = game.tasks
        startGameSession(game)
    }

    fun getCurrentTask(): Task? {
        if (gameEnded) return null
        return tasks.getOrNull(currentTaskIndex)
    }

    private fun startGameSession(game: Game) {
        _gameSession.value = GameSession(
            sessionId = UUID.randomUUID().toString(),
            gameId = game.gameId
        )
    }

    fun advanceToNextTask() {
        if (currentTaskIndex < tasks.size - 1) {
            currentTaskIndex++
        } else {
            gameEnded = true
        }
    }

    fun onTaskReached(task: Task) {

    }

    fun getTasks(): List<Task> = tasks
}

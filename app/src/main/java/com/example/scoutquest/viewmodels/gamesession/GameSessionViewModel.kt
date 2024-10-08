package com.example.scoutquest.viewmodels.gamesession

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.data.models.GameSession
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.repositories.GameSessionRepository
import com.example.scoutquest.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GameSessionViewModel @Inject constructor(
    private val gameSessionRepository: GameSessionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _gameSession = MutableLiveData<GameSession?>()
    val gameSession: LiveData<GameSession?> = _gameSession

    private var tasks: List<Task> = listOf()
    private var currentTaskIndex by mutableStateOf(0)
    var activeTask by mutableStateOf<Task?>(null)

    var gameEnded by mutableStateOf(false)
        private set

    private var sessionId: String? = null

    val scores: MutableMap<Int, Int> = mutableMapOf()

    fun setGame(game: Game) {
        tasks = game.tasks
        if (sessionId == null) {
            startGameSession(game)
        }
    }

    private fun startGameSession(game: Game) {
        viewModelScope.launch {
            val userId = userRepository.getUserId()
            if (userId != null) {
                val newSession = GameSession(
                    sessionId = UUID.randomUUID().toString(),
                    gameId = game.gameId,
                    participants = listOf(userId) //user z index 0 jest kreatorem sesji gry
                )
                sessionId = newSession.sessionId
                gameSessionRepository.createGameSession(newSession)
                _gameSession.value = newSession
            } else {
                println("Error: User ID is null")
            }
        }
    }

    fun startGame(userId: String, sessionId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            user?.let {
                gameSessionRepository.addParticipantToGameSession(sessionId, it)
            }
        }
    }

    fun getCurrentTaskId(): Int {
        return tasks.getOrNull(currentTaskIndex)?.taskId ?: -1
    }

    fun totalScores(): Int {
        return scores.values.sum()
    }

    fun updateTaskScore(points: Int) {
        val taskId = getCurrentTaskId()
        scores[taskId] = points
        updateScoresInFirestore()

    }

    private fun updateScoresInFirestore() {
        viewModelScope.launch {
            sessionId?.let {
                    gameSessionRepository.updateScores(it, scores)
                }
            }
        }

    fun resetGameSession() {
        tasks = emptyList()
        currentTaskIndex = 0
        activeTask = null
        gameEnded = false
        _gameSession.value = null
        sessionId = null
        scores.clear()
    }

    fun getCurrentTask(): Task? {
        if (gameEnded) return null
        return tasks.getOrNull(currentTaskIndex)
    }

    private fun advanceToNextTask() {
        if (currentTaskIndex < tasks.size - 1) {
            currentTaskIndex++
            updateGameSession()
        } else {
            currentTaskIndex = tasks.size //ustawiamy na liczbę zadań bo wykonano ostatnie zadanie
            gameEnded = true
            markGameSessionAsFinished()
        }
    }


    fun onTaskCompleted() {
        advanceToNextTask()
        activeTask = null
    }

    fun onTaskReached(task: Task) {
        activeTask = task
    }

    fun getTasks(): List<Task> = tasks

    private fun updateGameSession() {
        viewModelScope.launch {
            _gameSession.value?.let { session ->
                val updatedSession = session.copy(currentTaskIndex = currentTaskIndex)
                gameSessionRepository.updateGameSession(session.sessionId, updatedSession)
                _gameSession.value = updatedSession
            }
        }
    }

    private fun markGameSessionAsFinished() {
        viewModelScope.launch {
            _gameSession.value?.let { session ->
                val finishedSession = session.copy(
                    finished = true,
                    currentTaskIndex = tasks.size
                )
                gameSessionRepository.updateGameSession(session.sessionId, finishedSession)
                _gameSession.value = finishedSession
            }
        }
    }

}


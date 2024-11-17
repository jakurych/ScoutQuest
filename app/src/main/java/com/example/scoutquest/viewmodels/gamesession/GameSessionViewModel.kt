package com.example.scoutquest.viewmodels.gamesession

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.scoutquest.data.repositories.OpenTaskRepository
import com.example.scoutquest.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class GameSessionViewModel @Inject constructor(
    private val gameSessionRepository: GameSessionRepository,
    private val userRepository: UserRepository,
    private val openTaskRepository: OpenTaskRepository
) : ViewModel() {

    //openWorld
    private val _openWorldTasks = MutableStateFlow<List<Task>>(emptyList())
    val openWorldTasks = _openWorldTasks.asStateFlow()

    private val _completedOpenTasks = MutableStateFlow<Set<String>>(emptySet())
    val completedOpenTasks = _completedOpenTasks.asStateFlow()

    //game
    private val _gameSession = MutableLiveData<GameSession?>()
    val gameSession: LiveData<GameSession?> = _gameSession

    private var tasks: List<Task> = listOf()
    private var currentTaskIndex by mutableIntStateOf(0)
    var activeTask by mutableStateOf<Task?>(null)

    var gameEnded by mutableStateOf(false)
        private set

    private var sessionId: String? = null

    private val scores: MutableMap<String, Long> = mutableMapOf()

    //openWorld

    fun loadOpenWorldTasks() {
        viewModelScope.launch {
            val tasks = openTaskRepository.getAllOpenTasks()
            _openWorldTasks.value = tasks
        }
    }

    fun updateOpenTaskScore(taskId: String, points: Int) {
        viewModelScope.launch {
            val userId = userRepository.getUserId()
            if (userId != null) {
                userRepository.updateUserPoints(userId, points)
                _completedOpenTasks.value = _completedOpenTasks.value + taskId
                openTaskRepository.markTaskAsCompleted(userId, taskId)
            }
        }
    }

    fun isTaskCompleted(taskId: String): Boolean {
        return completedOpenTasks.value.contains(taskId)
    }



    //guided adventure

    fun setGame(game: Game) {
        tasks = game.tasks
        if (sessionId == null) {
            startGameSession(game)
        } else {
            loadScoresFromFirebase()
        }
    }

    private fun startGameSession(game: Game) {
        viewModelScope.launch {
            val userId = userRepository.getUserId()
            if (userId != null) {
                val newSession = GameSession(
                    sessionId = UUID.randomUUID().toString(),
                    gameId = game.gameId,
                    participants = listOf(userId)
                )
                sessionId = newSession.sessionId
                gameSessionRepository.createGameSession(newSession)
                _gameSession.value = newSession
                loadScoresFromFirebase()
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

    private fun getCurrentTaskId(): Int {
        return tasks.getOrNull(currentTaskIndex)?.taskId ?: -1
    }

    fun totalScores(): Long {
        return scores.values.sum()
    }

    fun updateTaskScore(points: Int) {
        val taskId = getCurrentTaskId().toString()
        scores[taskId] = points.toLong()
        updateScoresInFirestore()
    }

    private fun updateScoresInFirestore() {
        viewModelScope.launch {
            sessionId?.let {
                gameSessionRepository.updateScores(it, scores)
            }
        }
    }

    private fun loadScoresFromFirebase() {
        viewModelScope.launch {
            sessionId?.let { id ->
                val session = gameSessionRepository.getGameSessionById(id)
                session?.scores?.let { firebaseScores ->
                    scores.clear()
                    scores.putAll(firebaseScores)
                    println("Scores loaded from Firebase: $scores")
                }
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
            currentTaskIndex = tasks.size
            gameEnded = true
            markGameSessionAsFinished()
        }
    }

    fun onTaskCompleted() {
        advanceToNextTask()
        activeTask = null
        loadScoresFromFirebase()
    }

    fun onTaskReached(task: Task) {
        activeTask = task
    }

    fun getTasks(): List<Task> = tasks

    private fun updateGameSession() {
        viewModelScope.launch {
            _gameSession.value?.let { session ->
                val updatedSession = session.copy(
                    currentTaskIndex = currentTaskIndex,
                    scores = scores.toMap()
                )
                gameSessionRepository.updateGameSession(session.sessionId, updatedSession)
                _gameSession.value = updatedSession
            }
        }
    }

    private fun updateUserProfileAfterGame() {
        viewModelScope.launch {
            val userId = userRepository.getUserId()
            if (userId != null) {
                val totalPoints = totalScores().toInt()
                userRepository.updateUserPoints(userId, totalPoints)
                sessionId?.let { id ->
                    userRepository.addGameToUserHistory(userId, id)
                }
            } else {
                println("Error: User ID is null when trying to update profile after game")
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

                updateUserProfileAfterGame()
            }
        }
    }
}

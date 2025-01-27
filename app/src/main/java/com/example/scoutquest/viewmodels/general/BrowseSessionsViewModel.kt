package com.example.scoutquest.viewmodels.general

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.data.models.GameSession
import com.example.scoutquest.data.repositories.GameRepository
import com.example.scoutquest.data.repositories.GameSessionRepository
import com.example.scoutquest.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseSessionsViewModel @Inject constructor(
    private val gameSessionRepository: GameSessionRepository,
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _activeSessions = MutableStateFlow<List<GameSession>>(emptyList())
    val activeSessions: StateFlow<List<GameSession>> = _activeSessions.asStateFlow()

    private val _gameData = MutableStateFlow<GameData?>(null)
    val gameData: StateFlow<GameData?> = _gameData.asStateFlow()

    //inf o grze
    data class GameData(
        val name: String,
        val description: String,
        val creatorId: String
    )

    suspend fun loadActiveSessions() {
        val userId = userRepository.getUserId()
        val user = userId?.let { userRepository.getUserById(it) }

        val userGameHistory = user?.gamesHistory ?: emptyList()

        val sessions = gameSessionRepository.getActiveSessions()
        _activeSessions.value = sessions.filter { session ->
            !session.finished && session.sessionId in userGameHistory
        }
    }


    suspend fun getGameData(gameId: String): GameData? {
        return try {
            val game = gameRepository.getGameById(gameId)
            game?.let {
                GameData(
                    name = it.name,
                    description = it.description,
                    creatorId = it.creatorId
                )
            }
        } catch (e: Exception) {
            println("Error fetching game data: ${e.message}")
            null
        }
    }

    suspend fun getGameFromSession(session: GameSession): Game? {
        return gameRepository.getGameById(session.gameId)
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            val userId = userRepository.getUserId()
            userId?.let {
                userRepository.removeSessionFromUserHistory(it, sessionId)
            }
            gameSessionRepository.deleteGameSession(sessionId)
            loadActiveSessions()
        }
    }

}

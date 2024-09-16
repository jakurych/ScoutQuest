package com.example.scoutquest.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.repositories.GameRepository
import com.example.scoutquest.data.repositories.UserRepository
import com.example.scoutquest.gameoperations.GameCalculations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseGamesViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val application: Application
) : ViewModel() {

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games

    private val gameCalculations = GameCalculations(application.applicationContext)

    init {
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            _games.value = gameRepository.getAllGames()
        }
    }

    fun determineCities(tasks: List<Task>): List<String> {
        return gameCalculations.determineCities(tasks)
    }

    fun calculateTotalDistance(tasks: List<Task>): String {
        return gameCalculations.calculateTotalDistance(tasks)
    }

    suspend fun getCreatorUsername(creatorId: String): String? {
        val user = userRepository.getUserById(creatorId)
        return user?.username
    }
}

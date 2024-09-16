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
    application: Application
) : ViewModel() {

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games

    private val _filteredGames = MutableStateFlow<List<Game>>(emptyList())
    val filteredGames: StateFlow<List<Game>> = _filteredGames

    private val gameCalculations = GameCalculations(application.applicationContext)

    private var selectedCities = mutableSetOf<String>()
    private var minDistance: Double? = null
    private var maxDistance: Double? = null

    init {
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            val allGames = gameRepository.getAllGames()
            _games.value = allGames
            _filteredGames.value = allGames
        }
    }

    fun determineCities(tasks: List<Task>): List<String> {
        return gameCalculations.determineCities(tasks)
    }

    fun calculateTotalDistance(tasks: List<Task>): Double {
        val distanceString = gameCalculations.calculateTotalDistance(tasks).toString()
        return distanceString.replace(",", ".").toDoubleOrNull() ?: 0.0
    }

    suspend fun getCreatorUsername(creatorId: String): String? {
        val user = userRepository.getUserById(creatorId)
        return user?.username
    }

    fun updateCityFilter(selectedCities: Set<String>) {
        this.selectedCities = selectedCities.toMutableSet()
        applyFilters()
    }

    fun updateDistanceFilter(minDistance: Double?, maxDistance: Double?) {
        this.minDistance = minDistance
        this.maxDistance = maxDistance
        applyFilters()
    }

    private fun applyFilters() {
        val currentMinDistance = minDistance
        val currentMaxDistance = maxDistance
        val filtered = _games.value.filter { game ->
            val gameCities = determineCities(game.tasks)
            val distance = try {
                calculateTotalDistance(game.tasks)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                0.0
            }

            val cityMatch = selectedCities.isEmpty() || gameCities.any { it in selectedCities }
            val distanceMatch = (currentMinDistance == null || distance >= currentMinDistance) &&
                    (currentMaxDistance == null || distance <= currentMaxDistance)

            cityMatch && distanceMatch
        }
        _filteredGames.value = filtered
    }
}

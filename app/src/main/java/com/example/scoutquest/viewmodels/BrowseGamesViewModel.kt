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
import kotlinx.coroutines.flow.*
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val gameCalculations = GameCalculations(application.applicationContext)

    private var selectedCities = mutableSetOf<String>()
    private var minDistance: Double? = null
    private var maxDistance: Double? = null

    private val _sortByDistance = MutableStateFlow(true)
    val sortByDistance: StateFlow<Boolean> = _sortByDistance

    private val _ascendingOrder = MutableStateFlow(true)
    val ascendingOrder: StateFlow<Boolean> = _ascendingOrder

    init {
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            val allGames = gameRepository.getAllGames()
            _games.value = allGames
            applyFiltersAndSort()
        }
    }

    fun removeGame(gameId: String, userId: String) {
        viewModelScope.launch {
            gameRepository.removeGame(gameId)
            userRepository.removeGameFromUser(userId, gameId)
            loadUserGames(userId)
        }
    }


    fun loadUserGames(userId: String) {
        viewModelScope.launch {
            val userGames = gameRepository.getGamesByUserId(userId)
            _games.value = userGames
            applyFiltersAndSort()
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
        applyFiltersAndSort()
    }

    fun updateDistanceFilter(minDistance: Double?, maxDistance: Double?) {
        this.minDistance = minDistance
        this.maxDistance = maxDistance
        applyFiltersAndSort()
    }

    fun toggleSortCriteria() {
        _sortByDistance.value = !_sortByDistance.value
        applyFiltersAndSort()
    }

    fun toggleSortOrder() {
        _ascendingOrder.value = !_ascendingOrder.value
        applyFiltersAndSort()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
        val currentMinDistance = minDistance
        val currentMaxDistance = maxDistance
        val currentQuery = _searchQuery.value

        var filtered = _games.value.filter { game ->
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
            val searchMatch = currentQuery.isBlank() || game.name.contains(currentQuery, ignoreCase = true) ||
                    game.description.contains(currentQuery, ignoreCase = true)

            cityMatch && distanceMatch && searchMatch
        }

        filtered = if (_sortByDistance.value) {
            if (_ascendingOrder.value) {
                filtered.sortedBy { calculateTotalDistance(it.tasks) }
            } else {
                filtered.sortedByDescending { calculateTotalDistance(it.tasks) }
            }
        } else {
            if (_ascendingOrder.value) {
                filtered.sortedBy { it.rating?.averageRating ?: 0.0f }
            } else {
                filtered.sortedByDescending { it.rating?.averageRating ?: 0.0f }
            }
        }

        _filteredGames.value = filtered
    }
}

package com.example.scoutquest.viewmodels

import android.app.Application
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.repositories.GameRepository
import com.example.scoutquest.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.math.*

@HiltViewModel
class BrowseGamesViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val application: Application
) : ViewModel() {

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games

    private val context: Context get() = application.applicationContext


    init {
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            _games.value = gameRepository.getAllGames()
        }
    }

    fun determineCities(tasks: List<Task>): List<String> {
        val geocoder = Geocoder(context, Locale.getDefault())
        val cities = mutableSetOf<String>()

        for (task in tasks) {
            try {
                val addresses = geocoder.getFromLocation(task.latitude, task.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality ?: "Unknown"
                    cities.add(city)
                } else {
                    cities.add("Mystery City")
                }
            } catch (e: Exception) {
                cities.add("Can't geocode city :(")
            }
        }

        return cities.toList()
    }



    fun calculateTotalDistance(tasks: List<Task>): String {
        var totalDistance = 0.0
        for (i in 0 until tasks.size - 1) {
            totalDistance += haversine(
                tasks[i].latitude, tasks[i].longitude,
                tasks[i + 1].latitude, tasks[i + 1].longitude
            )
        }
        return "%.2f".format(totalDistance)
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 //R of earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    suspend fun getCreatorUsername(creatorId: String): String? {
        val user = userRepository.getUserById(creatorId)
        return user?.username
    }
}

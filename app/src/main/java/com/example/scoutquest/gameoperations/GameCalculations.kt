package com.example.scoutquest.gameoperations

import android.content.Context
import android.location.Geocoder
import com.example.scoutquest.data.models.Task
import java.util.Locale
import kotlin.math.*

class GameCalculations(private val context: Context) {

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
        val R = 6371.0 // Promień Ziemi w km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}

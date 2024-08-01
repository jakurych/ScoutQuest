package com.example.scoutquest.data.models

import android.location.Location

data class Task(
    var taskId: Int,
    var sequenceNumber: Int,
    var title: String? = null, // Optional field
    var description: String,
    var points: Int,
    var location: Location,
    var gameId: Int,
    var markerColor: String
)

package com.example.scoutquest.data.models

data class Task(
    var taskId: Int = 0,
    var sequenceNumber: Int = 0,
    var title: String? = null,
    var description: String = "",
    var points: Int = 0,
    var latitude: Double = 0.0, //Double dla szerokości geograficznej
    var longitude: Double = 0.0, //Double dla długości geograficznej
    var gameId: Int = 0,
    var markerColor: String = ""
)
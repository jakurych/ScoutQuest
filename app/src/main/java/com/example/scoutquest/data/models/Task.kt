package com.example.scoutquest.data.models

import android.location.Location

data class Task(
    val taskId: String,
    val gameId: Int,
    val description: String,
    val location: Location,
    val points: Int,
    val interactionType: String, //wprowadz odp na pytanie, odnajdz beacon,
    //znajdz i zeskanuj Qr,
    var status: String?,
    var sequenceNumber: Int
)
package com.example.scoutquest.data.models

data class Game(
    val gameId: Int,
    val creator: User,
    val name: String,
    val description: String,
    val tasks: List<Task>,
    var isPublic: Boolean
)
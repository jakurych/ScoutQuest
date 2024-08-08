package com.example.scoutquest.data.models

data class Game(
    val gameId: Int,
    val creator: String?,
    val name: String,
    val description: String,
    val tasks: List<Task>,
    val isPublic: Boolean
)

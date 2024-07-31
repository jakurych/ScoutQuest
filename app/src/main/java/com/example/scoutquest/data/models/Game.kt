package com.example.scoutquest.data.models

data class Game(
    val gameId: Int,
    val creator: String?, // Placeholder for the creator
    val name: String,
    val description: String,
    val tasks: List<Task>,
    val isPublic: Boolean
)

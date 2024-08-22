package com.example.scoutquest.data.models

data class Game(
    val gameId: Int = 0,
    val creator: String? = null,
    val name: String = "",
    val description: String = "",
    val tasks: List<Task> = emptyList(),
    val isPublic: Boolean = false
)
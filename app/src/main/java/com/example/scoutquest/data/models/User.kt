package com.example.scoutquest.data.models

data class User(
    val userId: String? = null,
    val username: String = "",
    val password: String = "",
    val email: String? = "",
    val badges: List<Badge>? = emptyList(),
    val profilePictureUrl: String? = null, //URL-a obrazka
    val points: Int = 0,
    val createdGames: List<String>? = null,
    val gamesHistory: List<String>? = null
)
package com.example.scoutquest.data.models

import android.graphics.Picture

data class User (
    val userId: Int? = null,
    val username: String,
    val password: String,
    val email: String?,
    val badges: List<Bagdes>? = null,
    val profilePicture: Picture? = null,
    val points: Int = 0,
    val createdGames: List<Game>? = null,
    val gamesHistory: List<Session>? = null
)
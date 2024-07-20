package com.example.scoutquest.data.models

import android.graphics.Picture

data class User (
    val userId: Int,
    val username: String,
    val password: String,
    val email: String,
    val badges: List<Bagdes>,
    val profilePicture: Picture,
    val points: Int
)
package com.example.scoutquest.data.models

data class Comment(
    val userId: String,
    val userName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

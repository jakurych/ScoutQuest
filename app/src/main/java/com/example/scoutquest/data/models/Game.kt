package com.example.scoutquest.data.models

import com.google.firebase.firestore.DocumentId

data class Game(
    @DocumentId
    val gameId: String = "",
    val creatorId: String,
    val creatorEmail: String,
    val name: String,
    val description: String,
    val tasks: List<Task>,
    val isPublic: Boolean,
    val playCount: Int? = null,
    val rating: Rating? = null
)

package com.example.scoutquest.data.models.tasktypes

data class Note(
    val taskType: String = "NoteList",
    val notes: List<String> = emptyList()
)
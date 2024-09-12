package com.example.scoutquest.data.models.tasktypes

data class Note(
    override val taskType: String = "NoteList",
    val notes: List<String> = emptyList()
) : TaskType

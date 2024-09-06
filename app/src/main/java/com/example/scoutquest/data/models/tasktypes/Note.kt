package com.example.scoutquest.data.models.tasktypes

data class Note(
    val text: String
) : TaskType {
    override val taskType: String = "Note"
}
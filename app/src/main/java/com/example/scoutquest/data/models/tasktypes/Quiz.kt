package com.example.scoutquest.data.models.tasktypes

data class Quiz(
    val taskType: String = "Quiz",
    val questions: List<Question> = emptyList()
)

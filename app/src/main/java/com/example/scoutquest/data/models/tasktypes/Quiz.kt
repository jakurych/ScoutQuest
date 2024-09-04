package com.example.scoutquest.data.models.tasktypes

data class Quiz (
    override val taskType: String = "Quiz",
    val questions: List<Question>
) : TaskType
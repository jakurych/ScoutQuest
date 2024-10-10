package com.example.scoutquest.data.models.tasktypes

data class OpenQuestion(
    val taskType: String = "OpenQuestion",
    val question: String = "",
    val expectedTopics: List<String>
)
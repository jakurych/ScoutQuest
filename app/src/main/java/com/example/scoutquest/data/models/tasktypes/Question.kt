package com.example.scoutquest.data.models.tasktypes

data class Question (
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: List<Int> = emptyList()
)
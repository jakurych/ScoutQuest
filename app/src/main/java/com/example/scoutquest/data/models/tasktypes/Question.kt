package com.example.scoutquest.data.models.tasktypes

data class Question (
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: List<Int>
)
package com.example.scoutquest.data.models.tasktypes

data class TrueFalse (
    val taskType: String = "TrueFalse",
    val questionsTf: List<String> = emptyList(),
    val answersTf: List<Boolean> = emptyList()

    )




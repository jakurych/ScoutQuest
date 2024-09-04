package com.example.scoutquest.data.models.tasktypes

data class None (
    override val taskType: String = "None",
    val infoText: String
) : TaskType
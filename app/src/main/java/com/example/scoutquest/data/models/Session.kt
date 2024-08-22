package com.example.scoutquest.data.models

data class Session(
    var sessionId: Int = 0,
    var gameId: Int = 0, // Zmienione z Game na Int
    var participants: List<User>? = emptyList(),
    var teamsScores: List<Team>? = emptyList(),
    var scores: Map<Int, Int>? = emptyMap(),
    var routingType: Int = 0,
    var tasks: Map<Task, Boolean> = emptyMap()
)
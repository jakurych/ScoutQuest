package com.example.scoutquest.data.models

data class Team(
    var teamId: Int = 0,
    var teamName: String? = null,
    var teamScore: Int? = 0,
    var teamLeader: User? = null,
    var teamMembers: List<User> = emptyList()
)
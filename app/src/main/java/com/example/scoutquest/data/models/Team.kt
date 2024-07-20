package com.example.scoutquest.data.models

data class Team (
    var teamId: Int,
    var teamName: String,
    var teamScore: Int,
    var teamLeader: User,
    var teamMembers: List<User>

)
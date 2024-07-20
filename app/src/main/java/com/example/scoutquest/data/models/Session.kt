package com.example.scoutquest.data.models

data class Session (
    var sessionId: Int,
    var gameId: Game,
    var participants: List<User>?,
    var teamsScores: List<Team>?,
    var scores: Map<Int, Int>?, // userId/teamId , score
    var routingType: Int
    //kolejnosc taskow ustalona przez twórce, gracze wychodzą co t
    //algorytm trasowania i rekompensata punktowa za odleglosc
    //gdy graczy duzo -> oba 
)
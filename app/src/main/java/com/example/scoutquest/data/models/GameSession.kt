package com.example.scoutquest.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class GameSession(
    val sessionId: String = "", // unikatowe ID sesji
    val gameId: String = "",
    val participants: List<String> = listOf(), // lista graczy
    val teamsScores: @RawValue List<Team> = listOf(), // wyniki zespołów
    var currentTaskIndex: Int = 0,
    val scores: Map<String, Long> = mapOf(), // mapa: taskId w grze -> zdobyte
    val finished: Boolean = false
) : Parcelable

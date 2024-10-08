package com.example.scoutquest.utils

class ConvertionOperations {

    fun convertScoresToIntKeys(scores: Map<String, Int>): Map<Int, Int> {
        return scores.mapKeys { it.key.toInt() }
    }

}
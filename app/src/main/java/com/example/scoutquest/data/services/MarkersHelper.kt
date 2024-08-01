package com.example.scoutquest.data.services

object MarkersHelper {
    private const val BASE_URL = "https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_"

    fun getMarkerUrl(color: String, character: String): String {
        return "$BASE_URL$color$character.png"
    }
}

package com.example.scoutquest.data.models

data class Badge(
    var badgeId: Int = 0,
    var name: String = "",
    var description: String = "",
    var iconUrl: String = "" // Zmienione z Picture na String dla URL-a obrazka
)
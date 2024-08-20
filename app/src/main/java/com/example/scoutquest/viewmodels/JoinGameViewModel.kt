package com.example.scoutquest.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class JoinGameViewModel : ViewModel() {

    var gameCode by mutableStateOf("")

    fun joinGame(){

    }
}
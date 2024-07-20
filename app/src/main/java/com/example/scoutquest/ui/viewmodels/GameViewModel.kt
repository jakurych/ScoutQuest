package com.example.scoutquest.ui.viewmodels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.data.repositories.GameRepository



class GameViewModel(private val repository: GameRepository) : ViewModel() {
    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> get() = _games

    fun addGame(game: Game) {
        repository.addGame(game)
        _games.value = repository.getAllGames()
    }

    fun removeGame(gameId: String) {
        repository.removeGame(gameId)
        _games.value = repository.getAllGames()

    }
}
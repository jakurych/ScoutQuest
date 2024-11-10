package com.example.scoutquest.viewmodels.tasktypes

import androidx.lifecycle.ViewModel
import com.example.scoutquest.data.models.tasktypes.OpenQuestion
import com.example.scoutquest.viewmodels.general.CreateNewGameViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OpenQuestionViewModel @Inject constructor() : ViewModel() {
    private lateinit var createNewGameViewModel: CreateNewGameViewModel

    private val _openQuestion = MutableStateFlow<OpenQuestion?>(null)
    val openQuestion: StateFlow<OpenQuestion?> = _openQuestion

    private val _hasOpenQuestion = MutableStateFlow(false)
    val hasOpenQuestion: StateFlow<Boolean> = _hasOpenQuestion

    fun setCreateNewGameViewModel(viewModel: CreateNewGameViewModel) {
        createNewGameViewModel = viewModel
    }

    fun setOpenQuestion(question: String, answer: String) {
        _openQuestion.update {
            OpenQuestion(taskType = "OpenQuestion", question = question, answer = answer)
        }
        _hasOpenQuestion.value = true
        createNewGameViewModel.currentOpenQuestionDetails = _openQuestion.value
        createNewGameViewModel.setTaskDetailsEntered(true)
    }

    fun getCurrentOpenQuestion(): OpenQuestion? {
        return _openQuestion.value
    }

    fun setOpenQuestionFromTask(openQuestion: OpenQuestion?) {
        _openQuestion.value = openQuestion
        _hasOpenQuestion.value = openQuestion != null
    }

    fun resetOpenQuestion() {
        _hasOpenQuestion.value = false
        _openQuestion.value = null
    }
}

package com.example.scoutquest.viewmodels.tasktypes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.tasktypes.TrueFalse
import com.example.scoutquest.viewmodels.general.CreateNewGameViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TrueFalseViewModel @Inject constructor() : ViewModel() {
    private lateinit var createNewGameViewModel: CreateNewGameViewModel

    private val _questionsTf = MutableStateFlow<List<String>>(emptyList())
    val questionsTf: StateFlow<List<String>> = _questionsTf

    private val _answersTf = MutableStateFlow<List<Boolean>>(emptyList())
    val answersTf: StateFlow<List<Boolean>> = _answersTf

    private val _taskDetailsEntered = MutableStateFlow(false)
    val hasQuestions: StateFlow<Boolean> = _questionsTf.map { it.isNotEmpty() }.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val taskDetailsEntered: StateFlow<Boolean> = _taskDetailsEntered

    fun setCreateNewGameViewModel(viewModel: CreateNewGameViewModel) {
        createNewGameViewModel = viewModel
    }

    fun addQuestion(question: String, answer: Boolean) {
        _questionsTf.value = _questionsTf.value + question
        _answersTf.value = _answersTf.value + answer
        _taskDetailsEntered.value = true
    }

    fun getCurrentTrueFalse(): TrueFalse {
        return TrueFalse(
            questionsTf = _questionsTf.value,
            answersTf = _answersTf.value
        )
    }

    fun setQuestionsFromTrueFalse(trueFalse: TrueFalse?) {
        _questionsTf.value = trueFalse?.questionsTf ?: emptyList()
        _answersTf.value = trueFalse?.answersTf ?: emptyList()
        _taskDetailsEntered.value = _questionsTf.value.isNotEmpty()
    }

    fun saveCurrentTrueFalse() {
        val currentTrueFalse = getCurrentTrueFalse()
        createNewGameViewModel.currentTrueFalseDetails = currentTrueFalse
        //resetTrueFalse()
    }

    fun resetTrueFalse() {
        _questionsTf.value = emptyList()
        _answersTf.value = emptyList()
        _taskDetailsEntered.value = false
    }

    fun removeQuestion(index: Int) {
        _questionsTf.value = _questionsTf.value.toMutableList().apply { removeAt(index) }
        _answersTf.value = _answersTf.value.toMutableList().apply { removeAt(index) }
    }

    fun setTaskDetailsEntered(entered: Boolean) {
        _taskDetailsEntered.value = entered
    }
}

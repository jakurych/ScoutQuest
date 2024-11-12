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
    private val _questionsTf = MutableStateFlow<List<String>>(emptyList())
    val questionsTf: StateFlow<List<String>> = _questionsTf

    private val _answersTf = MutableStateFlow<List<Boolean>>(emptyList())
    val answersTf: StateFlow<List<Boolean>> = _answersTf

    val hasQuestions: StateFlow<Boolean> = _questionsTf.map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun addQuestion(question: String, answer: Boolean) {
        _questionsTf.value = _questionsTf.value + question
        _answersTf.value = _answersTf.value + answer
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
    }

    fun resetTrueFalse() {
        _questionsTf.value = emptyList()
        _answersTf.value = emptyList()
    }

    fun removeQuestion(index: Int) {
        _questionsTf.value = _questionsTf.value.toMutableList().apply { removeAt(index) }
        _answersTf.value = _answersTf.value.toMutableList().apply { removeAt(index) }
    }
}

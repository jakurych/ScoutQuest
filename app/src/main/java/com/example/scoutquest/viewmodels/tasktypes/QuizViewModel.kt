package com.example.scoutquest.viewmodels.tasktypes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.scoutquest.data.models.tasktypes.Question

class QuizViewModel : ViewModel() {
    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _hasQuestions = MutableStateFlow(false)
    val hasQuestions: StateFlow<Boolean> = _hasQuestions

    fun addQuestion(question: Question) {
        _questions.update { currentQuestions ->
            val newQuestions = currentQuestions + question
            _hasQuestions.value = newQuestions.isNotEmpty()
            newQuestions
        }
    }

    fun removeQuestion(index: Int) {
        _questions.update { currentQuestions ->
            if (index in currentQuestions.indices) {
                val newQuestions = currentQuestions.toMutableList().apply { removeAt(index) }
                _hasQuestions.value = newQuestions.isNotEmpty()
                newQuestions
            } else {
                currentQuestions
            }
        }
    }

    fun resetQuestions() {
        _questions.value = emptyList()
        _hasQuestions.value = false
    }

    fun saveQuiz() {
        _hasQuestions.value = _questions.value.isNotEmpty()
    }

    fun swapQuestions(index1: Int, index2: Int) {
        _questions.update { currentQuestions ->
            if (index1 in currentQuestions.indices && index2 in currentQuestions.indices) {
                val mutableQuestions = currentQuestions.toMutableList()
                val temp = mutableQuestions[index1]
                mutableQuestions[index1] = mutableQuestions[index2]
                mutableQuestions[index2] = temp
                mutableQuestions
            } else {
                currentQuestions
            }
        }
    }
}

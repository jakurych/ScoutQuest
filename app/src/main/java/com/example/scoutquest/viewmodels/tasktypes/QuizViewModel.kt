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

    // Przechowywanie aktualnych danych quizu
    var currentQuestionText: String = ""
    var currentOptions: List<String> = listOf("", "")
    var currentCorrectAnswerIndices: List<Int> = emptyList()

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

    fun swapQuestions(fromIndex: Int, toIndex: Int) {
        _questions.update { currentQuestions ->
            val mutableList = currentQuestions.toMutableList()
            val item = mutableList.removeAt(fromIndex)
            mutableList.add(toIndex, item)
            mutableList
        }
    }

    fun resetQuestions() {
        _questions.value = emptyList()
        _hasQuestions.value = false
    }

    fun saveQuiz() {
        // Implementacja logiki zapisu quizu
    }
}

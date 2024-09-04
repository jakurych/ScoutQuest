package com.example.scoutquest.viewmodels.tasktypes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.scoutquest.data.models.tasktypes.Question

class QuizViewModel : ViewModel() {
    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    fun addQuestion(question: Question) {
        _questions.update { currentQuestions ->
            if (currentQuestions.size < 5) {
                currentQuestions + question
            } else {
                currentQuestions
            }
        }
    }

    fun resetQuestions() {
        _questions.value = emptyList()
    }
}

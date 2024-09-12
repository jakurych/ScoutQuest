package com.example.scoutquest.viewmodels.tasktypes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.scoutquest.data.models.tasktypes.Question
import com.example.scoutquest.data.models.tasktypes.Quiz
import com.example.scoutquest.viewmodels.CreateNewGameViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor() : ViewModel() {
    private lateinit var createNewGameViewModel: CreateNewGameViewModel

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _hasQuestions = MutableStateFlow(false)
    val hasQuestions: StateFlow<Boolean> = _hasQuestions

    fun setCreateNewGameViewModel(viewModel: CreateNewGameViewModel) {
        createNewGameViewModel = viewModel
    }

    fun addQuestion(question: Question) {
        _questions.update { currentQuestions ->
            val newQuestions = currentQuestions + question
            _hasQuestions.value = newQuestions.isNotEmpty()
            setTaskDetailsEntered(newQuestions.isNotEmpty())
            newQuestions
        }
    }

    fun saveCurrentQuiz() {
        val currentQuiz = getCurrentQuiz()
        createNewGameViewModel.currentQuizDetails = currentQuiz
    }

    fun removeQuestion(index: Int) {
        _questions.update { currentQuestions ->
            if (index in currentQuestions.indices) {
                val newQuestions = currentQuestions.toMutableList().apply { removeAt(index) }
                _hasQuestions.value = newQuestions.isNotEmpty()
                setTaskDetailsEntered(newQuestions.isNotEmpty())
                newQuestions
            } else {
                currentQuestions
            }
        }
    }

    fun swapQuestions(fromIndex: Int, toIndex: Int) {
        _questions.update { currentQuestions ->
            val mutableList = currentQuestions.toMutableList()
            if (fromIndex in mutableList.indices && toIndex in mutableList.indices) {
                val item = mutableList.removeAt(fromIndex)
                mutableList.add(toIndex, item)
            }
            mutableList
        }
    }

    fun getCurrentQuiz(): Quiz {
        return Quiz(questions = _questions.value)
    }

    fun setQuestionsFromQuiz(quiz: Quiz?) {
        _questions.value = quiz?.questions ?: emptyList()
        _hasQuestions.value = _questions.value.isNotEmpty()
        setTaskDetailsEntered(_questions.value.isNotEmpty())
    }

    fun resetQuiz() {
        _questions.value = emptyList()
        _hasQuestions.value = false
        setTaskDetailsEntered(false)
    }

    fun setTaskDetailsEntered(entered: Boolean) {
        createNewGameViewModel.setTaskDetailsEntered(entered)
    }
}

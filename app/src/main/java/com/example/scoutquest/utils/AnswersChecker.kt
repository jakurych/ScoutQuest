package com.example.scoutquest.utils

import com.example.scoutquest.data.models.tasktypes.Quiz
import com.example.scoutquest.data.models.tasktypes.TrueFalse
import com.example.scoutquest.data.models.tasktypes.Note

class AnswersChecker {

    //Points depends on task type
    var notePoints = 5
    var trueFalsePoints = 10
    var quizCorrectAnswerPoints = 10
    var quizBonusPoints = 5
    var endGameBonus = 7
    var taskReachedBonus = 1


    //Quiz check
    fun checkQuiz(quiz: Quiz, userAnswers: List<List<Int>>): Int {
        var totalPoints = 0
        var allCorrect = true

        quiz.questions.forEachIndexed { index, question ->
            if (index < userAnswers.size) {
                val userAnswer = userAnswers[index]
                if (userAnswer.sorted() == question.correctAnswerIndex.sorted()) {
                    totalPoints += quizCorrectAnswerPoints
                } else {
                    allCorrect = false
                }
            } else {
                allCorrect = false
            }
        }

        //Bonus if all correct
        if (allCorrect) {
            totalPoints += quizBonusPoints
        }

        return totalPoints
    }

    //True/False
    fun checkTrueFalse(trueFalse: TrueFalse, userAnswers: List<Boolean>): Int {
        var totalPoints = 0

        trueFalse.answersTf.forEachIndexed { index, correctAnswer ->
            if (index < userAnswers.size) {
                if (userAnswers[index] == correctAnswer) {
                    totalPoints += trueFalsePoints
                }
            }
        }

        return totalPoints
    }

    //note instant points
    fun checkNote(note: Note): Int {
        return notePoints
    }

    fun endGameBonus(): Int {
        return endGameBonus
    }

    fun taskReachedBonus(): Int {
        return taskReachedBonus
    }
}
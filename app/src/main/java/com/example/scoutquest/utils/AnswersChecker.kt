package com.example.scoutquest.utils

import android.content.Context
import android.util.Log
import com.example.scoutquest.data.models.tasktypes.Quiz
import com.example.scoutquest.data.models.tasktypes.TrueFalse
import com.example.scoutquest.data.models.tasktypes.Note
//import com.google.firebase.functions.FirebaseFunctions
//import com.google.cloud.v1.Document
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import org.json.JSONObject


class AnswersChecker {

    //Points depends on task type
    private var notePoints = 5
    private var trueFalsePoints = 10
    private var quizCorrectAnswerPoints = 10
    private var quizBonusPoints = 5
    private var endGameBonus = 7
    private var taskReachedBonus = 1
    private var openQuestionPoints = 20
    private var photoPoints = 15

    private val convertionOperations = ConvertionOperations()


    //Check photo question
    suspend fun checkPhoto(
        imageUri: String,
        description: String,
        context: Context
    ): Int {
        val functions = FirebaseFunctions.getInstance()

        try {
            val imageBase64 = convertionOperations.readImageAsBase64(imageUri, context)

            val data = hashMapOf(
                "imageBase64" to imageBase64,
                "description" to description
            )

            val result = functions
                .getHttpsCallable("checkPhotoFunction")
                .call(data)
                .continueWith { task ->
                    val resultData = task.result?.data as? Map<*, *>
                    (resultData?.get("score") as? Number)?.toInt() ?: 0
                }.await()

            return result
        } catch (e: Exception) {
            Log.e("AnswersChecker", "Error calling checkPhotoFunction", e)
            return 0
        }
    }

    //Open question check
    suspend fun checkOpenQuestion(
        playerAnswer: String,
        correctAnswer: String,
        question: String
    ): Int {
        val functions = FirebaseFunctions.getInstance()

        Log.d("AnswersChecker", "Checking open question: $question, $playerAnswer, $correctAnswer")

        val data = hashMapOf(
            "playerAnswer" to playerAnswer,
            "correctAnswer" to correctAnswer,
            "question" to question
        )

        Log.d("DataLogger", "Data content: $data")

        try {
            val result = functions
                .getHttpsCallable("checkOpenQuestionFunctionV2")
                .call(data)
                .continueWith { task ->
                    val resultData = task.result?.data as? Map<*, *>
                    (resultData?.get("score") as? Number)?.toInt() ?: 0
                }.await()

            Log.d("AnswersChecker", "Received score: $result")
            return result
        } catch (e: Exception) {
            Log.e("AnswersChecker", "Error calling cloud function", e)
            return 0
        }
    }



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

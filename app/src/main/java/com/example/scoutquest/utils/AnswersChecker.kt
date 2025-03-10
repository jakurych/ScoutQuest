package com.example.scoutquest.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.scoutquest.data.models.tasktypes.Quiz
import com.example.scoutquest.data.models.tasktypes.TrueFalse
import com.example.scoutquest.data.models.tasktypes.Note
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await


class AnswersChecker {


    //quiz -> 10 points for each correct answer, 5 bonus points for all correct
    //open question -> max 20 points
    //note -> 5 points
    //true/false -> 8 points for each correct answer, 5 bonus points for all correct
    //photo -> max 25 points


    //Points depends on task type
    private var notePoints = 5
    private var trueFalsePoints = 10
    private var quizCorrectAnswerPoints = 10
    private var trueFalseBonusPoints = 5
    private var quizBonusPoints = 5
    private var endGameBonus = 7
    private var taskReachedBonus = 1
    private var openQuestionPoints = 20
    private var photoPoints = 15

    private val convertionOperations = ConvertionOperations()


    suspend fun checkPhoto(
        imageUri: String,
        description: String,
        context: Context
    ): Int {
        val functions = FirebaseFunctions.getInstance()

        Log.d("AnswersChecker", "Task data: $description")

        try {
            val uri = Uri.parse(imageUri)

            //Kompresję obrazu
            val compressedImage = convertionOperations.compressImage(context, uri)
            if (compressedImage == null) {
                Log.e("AnswersChecker", "Failed to compress image")
                return 0
            }

            //Konwertuj skompresowany obraz na Base64
            val imageBase64 = Base64.encodeToString(compressedImage, Base64.DEFAULT)

            //Przygotuj dane do wysłania
            val data = hashMapOf(
                "imageBase64" to imageBase64,
                "description" to description
            )

            return functions
                .getHttpsCallable("checkPhotoFunction")
                .call(data)
                .continueWith { task ->
                    val response = task.result?.data as? Map<*, *>
                    when {
                        task.isSuccessful && response != null -> {
                            (response["score"] as? Number)?.toInt() ?: 0
                        }
                        else -> {
                            Log.e("AnswersChecker", "Function returned null or invalid response")
                            0
                        }
                    }
                }.await()

        } catch (e: Exception) {
            Log.e("AnswersChecker", "Error in checkPhoto", e)
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

    //klasa wynikowa Quizu
    data class QuizResult(
        val points: Int,
        val incorrectAnswers: List<Pair<Int, List<Int>>>
    )

    //Quiz check
    fun checkQuiz(quiz: Quiz, userAnswers: List<List<Int>>): QuizResult {
            var totalPoints = 0
            var allCorrect = true
            val incorrectAnswers = mutableListOf<Pair<Int, List<Int>>>()

            quiz.questions.forEachIndexed { index, question ->
                if (index < userAnswers.size) {
                    val userAnswer = userAnswers[index]
                    if (userAnswer.sorted() == question.correctAnswerIndex.sorted()) {
                        totalPoints += quizCorrectAnswerPoints
                    } else {
                        allCorrect = false
                        incorrectAnswers.add(index to question.correctAnswerIndex)
                    }
                } else {
                    allCorrect = false
                }
            }

            if (allCorrect) {
                totalPoints += trueFalseBonusPoints
            }

            return QuizResult(totalPoints, incorrectAnswers)
        }

    //True/False result
    data class TrueFalseResult(
        val points: Int,
        val incorrectAnswers: List<Int>
    )

    //True/False
    fun checkTrueFalse(trueFalse: TrueFalse, userAnswers: List<Boolean>): TrueFalseResult {
        var totalPoints = 0
        var allCorrect = true
        val incorrectAnswers = mutableListOf<Int>()

        trueFalse.answersTf.forEachIndexed { index, correctAnswer ->
            if (index < userAnswers.size) {
                if (userAnswers[index] == correctAnswer) {
                    totalPoints += trueFalsePoints
                } else {
                    allCorrect = false
                    incorrectAnswers.add(index)
                }
            } else {
                allCorrect = false
            }
        }

        if (allCorrect) {
            totalPoints += trueFalsePoints
        }

        return TrueFalseResult(totalPoints, incorrectAnswers)
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

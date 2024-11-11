package com.example.scoutquest.data.models

import android.os.Parcelable
import com.example.scoutquest.data.models.tasktypes.Note
import com.example.scoutquest.data.models.tasktypes.OpenQuestion
import com.example.scoutquest.data.models.tasktypes.Photo
import com.example.scoutquest.data.models.tasktypes.Quiz
import com.example.scoutquest.data.models.tasktypes.TrueFalse
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Task(
    var taskId: Int = 0, //sequenceNumber
    var sequenceNumber: Int = taskId,
    var title: String? = null,
    var description: String = "",
    var points: Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var markerColor: String = "",
    var taskType: String? = null,

    //open world tasks, no corelated with game
    var isOpenWorldTask: Boolean = false,
    var category: String? =null,

    //task types
    var quizDetails: @RawValue Quiz? = null,
    var noteDetails: @RawValue Note? = null,
    var trueFalseDetails: @RawValue TrueFalse? = null,
    //task type ideas
    //Open question, photo task, checked by AI
    var openQuestionDetails: @RawValue OpenQuestion? = null,
    var photoDetails: @RawValue Photo? = null
    //Photo checked by AI


) : Parcelable

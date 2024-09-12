package com.example.scoutquest.data.models
import android.os.Parcelable
import com.example.scoutquest.data.models.tasktypes.TaskType
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Task(
    var taskId: Int = 0,
    var sequenceNumber: Int = 0,
    var title: String? = null,
    var description: String = "",
    var points: Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var gameId: Int = 0,
    var markerColor: String = "",
    var taskType: String? = null,
    var taskDetails: @RawValue TaskType? = null
) : Parcelable

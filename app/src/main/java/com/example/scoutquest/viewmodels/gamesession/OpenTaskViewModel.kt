package com.example.scoutquest.viewmodels.gamesession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.repositories.OpenTaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.scoutquest.data.models.tasktypes.Quiz
import com.example.scoutquest.data.models.tasktypes.Note
import com.example.scoutquest.data.models.tasktypes.TrueFalse
import com.example.scoutquest.data.models.tasktypes.OpenQuestion
import com.example.scoutquest.data.models.tasktypes.Photo


@HiltViewModel
class OpenTaskViewModel @Inject constructor(
    private val repository: OpenTaskRepository
) : ViewModel() {

    private val _taskTitle = MutableStateFlow("")
    val taskTitle: StateFlow<String> = _taskTitle

    private val _taskDescription = MutableStateFlow("")
    val taskDescription: StateFlow<String> = _taskDescription

    private val _taskPoints = MutableStateFlow("")
    val taskPoints: StateFlow<String> = _taskPoints

    private val _latitude = MutableStateFlow(52.253126)
    val latitude: StateFlow<Double> = _latitude

    private val _longitude = MutableStateFlow(20.900157)
    val longitude: StateFlow<Double> = _longitude

    private val _markerColor = MutableStateFlow("blue")
    val markerColor: StateFlow<String> = _markerColor

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus

    private val _selectedTaskType = MutableStateFlow("None")
    val selectedTaskType: StateFlow<String> = _selectedTaskType

    private val _taskCategory = MutableStateFlow("")
    val taskCategory: StateFlow<String> = _taskCategory

    fun updateTitle(title: String) {
        _taskTitle.value = title
    }

    fun updateCategory(category: String) {
        _taskCategory.value = category
    }

    fun updateDescription(description: String) {
        _taskDescription.value = description
    }

    fun updatePoints(points: String) {
        _taskPoints.value = points
    }

    fun updateLatitude(lat: Double) {
        _latitude.value = lat
    }

    fun updateLongitude(lon: Double) {
        _longitude.value = lon
    }

    fun updateMarkerColor(color: String) {
        _markerColor.value = color
    }

    fun updateSelectedTaskType(type: String) {
        _selectedTaskType.value = type
    }

    fun saveOpenTask(
        quizDetails: Quiz?,
        noteDetails: Note?,
        trueFalseDetails: TrueFalse?,
        openQuestionDetails: OpenQuestion?,
        photoDetails: Photo?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _saveStatus.value = SaveStatus.Loading
                val task = Task(
                    title = _taskTitle.value,
                    description = _taskDescription.value,
                    points = _taskPoints.value.toIntOrNull() ?: 0,
                    latitude = _latitude.value,
                    longitude = _longitude.value,
                    markerColor = _markerColor.value,
                    taskType = _selectedTaskType.value,
                    quizDetails = quizDetails,
                    noteDetails = noteDetails,
                    trueFalseDetails = trueFalseDetails,
                    openQuestionDetails = openQuestionDetails,
                    photoDetails = photoDetails,
                    isOpenWorldTask = true,
                    category = _taskCategory.value
                )
                repository.addOpenTask(
                    task = task,
                    onSuccess = {
                        _saveStatus.value = SaveStatus.Success
                        onSuccess()
                    },
                    onFailure = { exception ->
                        _saveStatus.value = SaveStatus.Error(exception.message ?: "Unknown error")
                        onFailure(exception)
                    }
                )
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.Error(e.message ?: "Unknown error")
                onFailure(e)
            }
        }
    }
}

    sealed class SaveStatus {
        object Idle : SaveStatus()
        object Loading : SaveStatus()
        object Success : SaveStatus()
        data class Error(val message: String) : SaveStatus()
    }


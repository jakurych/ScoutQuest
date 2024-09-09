package com.example.scoutquest.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.models.tasktypes.TaskType
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateNewGameViewModel @Inject constructor() : ViewModel() {
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _isPublic = MutableStateFlow(false)
    val isPublic: StateFlow<Boolean> = _isPublic

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _selectedLatitude = MutableStateFlow(52.253126)
    private val _selectedLongitude = MutableStateFlow(20.900157)

    private val _temporaryMarker = MutableStateFlow<LatLng?>(null)
    private val _taskToEdit = MutableStateFlow<Task?>(null)
    val taskToEdit: StateFlow<Task?> = _taskToEdit

    private val _isReorderingEnabled = MutableStateFlow(false)
    val isReorderingEnabled: StateFlow<Boolean> = _isReorderingEnabled

    private val _highestTaskId = MutableStateFlow(0)

    private val _isTaskDetailsEntered = MutableStateFlow(false)
    val isTaskDetailsEntered: StateFlow<Boolean> = _isTaskDetailsEntered

    private val _selectedTaskType = MutableStateFlow("Quiz")
    val selectedTaskType: StateFlow<String> get() = _selectedTaskType

    fun setTaskDetailsEntered(entered: Boolean) {
        _isTaskDetailsEntered.value = entered
    }

    var currentTaskTitle: String = ""
    var currentTaskDescription: String = ""
    var currentTaskPoints: String = "0"
    var currentLatitude: Double = _selectedLatitude.value
    var currentLongitude: Double = _selectedLongitude.value
    var currentMarkerColor: String = "red"

    private var _currentTaskDetails: TaskType? = null
    var currentTaskDetails: TaskType?
        get() = _currentTaskDetails
        set(value) {
            _currentTaskDetails = value
            setTaskDetailsEntered(value != null)
        }


    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    fun onIsPublicChange(newIsPublic: Boolean) {
        _isPublic.value = newIsPublic
    }

    fun setSelectedTaskType(taskType: String) {
        _selectedTaskType.value = taskType
    }

    fun onLocationSelected(latitude: Double, longitude: Double) {
        _selectedLatitude.value = latitude
        _selectedLongitude.value = longitude
        _temporaryMarker.value = LatLng(latitude, longitude)
        currentLatitude = latitude
        currentLongitude = longitude
    }

    fun setTaskToEdit(task: Task?) {
        _taskToEdit.value = task
        if (task != null) {
            currentTaskTitle = task.title ?: ""
            currentTaskDescription = task.description
            currentTaskPoints = task.points.toString()
            currentLatitude = task.latitude
            currentLongitude = task.longitude
            currentMarkerColor = task.markerColor
        } else {
            currentTaskTitle = ""
            currentTaskDescription = ""
            currentTaskPoints = "0"
            currentLatitude = _selectedLatitude.value
            currentLongitude = _selectedLongitude.value
            currentMarkerColor = "red"
        }
    }

    fun removeTask(task: Task) {
        _tasks.update { currentTasks ->
            currentTasks.filter { it.taskId != task.taskId }
        }
    }

    fun moveTask(fromIndex: Int, toIndex: Int) {
        _tasks.update { currentTasks ->
            val mutableTasks = currentTasks.toMutableList()
            val task = mutableTasks.removeAt(fromIndex)
            mutableTasks.add(toIndex, task)
            mutableTasks
        }
    }

    fun toggleReordering() {
        _isReorderingEnabled.value = !_isReorderingEnabled.value
    }

    private fun generateNewTaskId(): Int {
        _highestTaskId.update { it + 1 }
        return _highestTaskId.value
    }

    fun addOrUpdateTask(task: Task) {
        _tasks.update { currentTasks ->
            val existingTaskIndex = currentTasks.indexOfFirst { it.taskId == task.taskId }

            if (existingTaskIndex >= 0) {
                currentTasks.toMutableList().apply {
                    set(existingTaskIndex, task)
                }
            } else {
                val newTask = task.copy(taskId = generateNewTaskId())
                currentTasks + newTask
            }
        }
    }

    fun saveGame() {

    }

}
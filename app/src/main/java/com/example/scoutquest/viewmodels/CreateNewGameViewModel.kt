package com.example.scoutquest.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.scoutquest.data.models.Task
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.atomic.AtomicInteger

class CreateNewGameViewModel : ViewModel() {
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

    private val taskIdGenerator = AtomicInteger(1)

    var currentTaskTitle: String = ""
    var currentTaskDescription: String = ""
    var currentTaskPoints: String = "0"
    var currentLatitude: Double = _selectedLatitude.value
    var currentLongitude: Double = _selectedLongitude.value
    var currentMarkerColor: String = "red"

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    fun onIsPublicChange(newIsPublic: Boolean) {
        _isPublic.value = newIsPublic
    }

    fun onLocationSelected(latitude: Double, longitude: Double) {
        _selectedLatitude.value = latitude
        _selectedLongitude.value = longitude
        _temporaryMarker.value = LatLng(latitude, longitude)
        currentLatitude = latitude
        currentLongitude = longitude
    }

    fun getSelectedLocation(): LatLng {
        return LatLng(_selectedLatitude.value, _selectedLongitude.value)
    }

    fun setTaskToEdit(task: Task) {
        _taskToEdit.value = task
        currentTaskTitle = task.title ?: ""
        currentTaskDescription = task.description
        currentTaskPoints = task.points.toString()
        currentLatitude = task.latitude
        currentLongitude = task.longitude
        currentMarkerColor = task.markerColor
    }

    fun addOrUpdateTask(task: Task) {
        _tasks.update { currentTasks ->
            val existingTaskIndex = currentTasks.indexOfFirst { it.taskId == task.taskId }
            if (existingTaskIndex >= 0) {
                currentTasks.toMutableList().apply {
                    set(existingTaskIndex, task)
                }
            } else {
                currentTasks + task
            }
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

    fun generateNewTaskId(): Int {
        return taskIdGenerator.getAndIncrement()
    }

    fun saveGame() {
        // Logic for saving the game
    }
}

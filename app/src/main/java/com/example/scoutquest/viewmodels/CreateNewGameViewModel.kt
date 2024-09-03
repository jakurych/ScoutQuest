package com.example.scoutquest.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.scoutquest.data.models.Task
import com.google.android.gms.maps.model.LatLng

class CreateNewGameViewModel : ViewModel() {
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _isPublic = MutableStateFlow(false)
    val isPublic: StateFlow<Boolean> = _isPublic

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _selectedLatitude = MutableStateFlow(0.0)
    val selectedLatitude: StateFlow<Double> = _selectedLatitude

    private val _selectedLongitude = MutableStateFlow(0.0)
    val selectedLongitude: StateFlow<Double> = _selectedLongitude

    private val _temporaryMarker = MutableStateFlow<LatLng?>(null)
    val temporaryMarker: StateFlow<LatLng?> = _temporaryMarker

    private val _taskToEdit = MutableStateFlow<Task?>(null)
    val taskToEdit: StateFlow<Task?> = _taskToEdit

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
    }

    fun setTaskToEdit(task: Task) {
        _taskToEdit.value = task
    }

    fun addOrUpdateTask(task: Task) {
        _tasks.update { currentTasks ->
            val existingTaskIndex = currentTasks.indexOfFirst { it.taskId == task.taskId }
            if (existingTaskIndex >= 0) {
                currentTasks.toMutableList().apply { set(existingTaskIndex, task) }
            } else {
                currentTasks + task
            }
        }
    }

    fun saveGame() {

    }
}

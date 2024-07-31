package com.example.scoutquest.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateNewGameViewModel : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> get() = _name

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> get() = _description

    private val _isPublic = MutableStateFlow(false)
    val isPublic: StateFlow<Boolean> get() = _isPublic

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> get() = _selectedLocation

    private var nextSequenceNumber = 1
    private val availableSequenceNumbers = mutableListOf<Int>()
    private var nextTaskId = 1

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    fun onIsPublicChange(newIsPublic: Boolean) {
        _isPublic.value = newIsPublic
    }

    fun onLocationSelected(location: Location) {
        _selectedLocation.value = location
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            val updatedTasks = _tasks.value.toMutableList()
            val existingTaskIndex = updatedTasks.indexOfFirst { it.taskId == task.taskId }
            if (existingTaskIndex != -1) {
                // Update  task
                updatedTasks[existingTaskIndex] = task
            } else {
                // nowy task
                val sequenceNumber = if (availableSequenceNumbers.isNotEmpty()) {
                    availableSequenceNumbers.removeAt(0)
                } else {
                    nextSequenceNumber++
                }
                task.sequenceNumber = sequenceNumber
                task.taskId = nextTaskId++
                updatedTasks.add(task)
            }
            _tasks.value = updatedTasks
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            val updatedTasks = _tasks.value.toMutableList()
            updatedTasks.remove(task)
            _tasks.value = updatedTasks
            availableSequenceNumbers.add(task.sequenceNumber)
        }
    }

    fun saveGame() {

    }
}

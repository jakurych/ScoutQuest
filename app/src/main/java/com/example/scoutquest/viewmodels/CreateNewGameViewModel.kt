package com.example.scoutquest.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.data.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng

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

    private var nextTaskId = 1
    private var nextGameId = 1

    private val _creator = MutableStateFlow<String?>(null) // Placeholder for the creator
    val creator: StateFlow<String?> get() = _creator

    private val _mapMarkers = MutableStateFlow<List<LatLng>>(emptyList())
    val mapMarkers: StateFlow<List<LatLng>> get() = _mapMarkers

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    fun onIsPublicChange(newIsPublic: Boolean) {
        _isPublic.value = newIsPublic
    }

    fun onLocationSelected(location: Location?) {
        _selectedLocation.value = location
        updateTemporaryMarker(location)
    }

    fun onCreatorChange(newCreator: String?) {
        _creator.value = newCreator
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            val updatedTasks = _tasks.value.toMutableList()
            val existingTaskIndex = updatedTasks.indexOfFirst { it.taskId == task.taskId }
            if (existingTaskIndex != -1) {
                //Update existing task
                updatedTasks[existingTaskIndex] = task.copy()
            } else {
                //Add new task
                val newTask = task.copy(
                    sequenceNumber = updatedTasks.size + 1,
                    taskId = nextTaskId++
                )
                updatedTasks.add(newTask)
            }
            reassignSequenceNumbers(updatedTasks)
            updateMapMarkers(updatedTasks)
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            val updatedTasks = _tasks.value.toMutableList()
            updatedTasks.remove(task)
            reassignSequenceNumbers(updatedTasks)
            updateMapMarkers(updatedTasks)
        }
    }

    fun updateTaskSequence(taskId: Int, newSequenceNumber: Int): Boolean {
        val updatedTasks = _tasks.value.toMutableList()
        val taskToMoveIndex = updatedTasks.indexOfFirst { it.taskId == taskId }

        if (taskToMoveIndex == -1 || newSequenceNumber < 1 || newSequenceNumber > updatedTasks.size) {
            return false
        }

        val taskToMove = updatedTasks.removeAt(taskToMoveIndex)
        updatedTasks.add(newSequenceNumber - 1, taskToMove)

        reassignSequenceNumbers(updatedTasks)
        updateMapMarkers(updatedTasks)
        return true
    }

    private fun reassignSequenceNumbers(tasks: MutableList<Task>) {
        tasks.forEachIndexed { index, task ->
            task.sequenceNumber = index + 1
        }
        _tasks.value = tasks.toList() // Force a new list instance to trigger recomposition
    }

    private fun updateMapMarkers(tasks: List<Task>) {
        _mapMarkers.value = tasks.mapNotNull { task -> task.location?.let { LatLng(it.latitude, it.longitude) } }
    }

    private val _temporaryMarker = MutableStateFlow<LatLng?>(null)
    val temporaryMarker: StateFlow<LatLng?> get() = _temporaryMarker

    private fun updateTemporaryMarker(location: Location?) {
        _temporaryMarker.value = location?.let { LatLng(it.latitude, it.longitude) }
    }

    fun saveGame() {
        viewModelScope.launch {
            val game = Game(
                gameId = nextGameId++,
                creator = _creator.value, // Placeholder for the creator
                name = _name.value,
                description = _description.value,
                tasks = _tasks.value,
                isPublic = _isPublic.value
            )
            // Logic to save the game, e.g., saving to a database or sending to a server
        }
    }
}

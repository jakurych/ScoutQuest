package com.example.scoutquest.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.data.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateNewGameViewModel @Inject constructor() : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> get() = _name

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> get() = _description

    private val _isPublic = MutableStateFlow(false)
    val isPublic: StateFlow<Boolean> get() = _isPublic

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    private val _selectedLatitude = MutableStateFlow<Double?>(null)
    val selectedLatitude: StateFlow<Double?> get() = _selectedLatitude

    private val _selectedLongitude = MutableStateFlow<Double?>(null)
    val selectedLongitude: StateFlow<Double?> get() = _selectedLongitude

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

    fun onLocationSelected(latitude: Double?, longitude: Double?) {
        _selectedLatitude.value = latitude
        _selectedLongitude.value = longitude
        updateTemporaryMarker(latitude, longitude)
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
        _mapMarkers.value = tasks.map { task -> LatLng(task.latitude, task.longitude) }
    }

    private val _temporaryMarker = MutableStateFlow<LatLng?>(null)
    val temporaryMarker: StateFlow<LatLng?> get() = _temporaryMarker

    private fun updateTemporaryMarker(latitude: Double?, longitude: Double?) {
        _temporaryMarker.value = if (latitude != null && longitude != null) {
            LatLng(latitude, longitude)
        } else {
            null
        }
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
            // Logic to save the game to a database
        }
    }
}

package com.example.scoutquest.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.Game
import com.example.scoutquest.data.models.Task
import com.example.scoutquest.data.models.tasktypes.Note
import com.example.scoutquest.data.models.tasktypes.Quiz
import com.example.scoutquest.data.repositories.GameRepository
import com.example.scoutquest.gameoperations.GameCalculations
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNewGameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val gameCalculations: GameCalculations
) : ViewModel() {

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

    private val _creatorMail = MutableStateFlow("")
    //val creatorMail: StateFlow<String> = _creatorMail

    var currentTaskTitle: String = ""
    var currentTaskDescription: String = ""
    var currentTaskPoints: String = "0"
    var currentLatitude: Double = _selectedLatitude.value
    var currentLongitude: Double = _selectedLongitude.value
    var currentMarkerColor: String = "red"

    // Task types
    var currentQuizDetails: Quiz? = null
    var currentNoteDetails: Note? = null

    init {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let { firebaseUser ->
            _creatorMail.value = firebaseUser.email ?: "Somehow error happen in getting email lol"
        }
    }

    fun setTaskDetailsEntered(entered: Boolean) {
        _isTaskDetailsEntered.value = entered
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
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Log.e("SaveGame", "No user logged in")
                return@launch
            }

            val newGame = Game(
                creatorId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                creatorEmail = _creatorMail.value,
                name = _name.value,
                description = _description.value,
                tasks = _tasks.value,
                isPublic = _isPublic.value
            )

            try {
                gameRepository.addGame(newGame)
                Log.d("SaveGame", "Game saved successfully to db")
            } catch (e: Exception) {
                Log.e("SaveGame", "Error saving game to db", e)
            }

            // Game data logs
            Log.d("GameData", "=== Game ===")
            Log.d("GameData", "Creator: ${_creatorMail.value}")
            Log.d("GameData", "Name: ${_name.value}")
            Log.d("GameData", "Description: ${_description.value}")
            Log.d("GameData", "Is Public: ${_isPublic.value}")
            Log.d("GameData", "Number of Tasks: ${_tasks.value.size}")

            // Task data logs
            _tasks.value.forEachIndexed { index, task ->
                Log.d("TaskData", "----- Task ${index + 1} -----")
                Log.d("TaskData", "Title: ${task.title ?: "No Title"}")
                Log.d("TaskData", "Description: ${task.description}")
                Log.d("TaskData", "Points: ${task.points}")
                Log.d("TaskData", "Location: (${task.latitude}, ${task.longitude})")
                Log.d("TaskData", "Marker Color: ${task.markerColor}")
                Log.d("TaskData", "Task Type: ${task.taskType}")

                // Task details logs
                task.quizDetails?.let { quiz ->
                    Log.d("TaskDetails", "---- Quiz Details ----")
                    quiz.questions.forEachIndexed { questionIndex, question ->
                        Log.d(
                            "TaskDetails",
                            "Question ${questionIndex + 1}: ${question.questionText}"
                        )
                        Log.d("TaskDetails", "Options: ${question.options.joinToString(", ")}")
                        Log.d(
                            "TaskDetails",
                            "Correct Answer Index: ${question.correctAnswerIndex.joinToString(", ")}"
                        )
                    }
                }

                task.noteDetails?.let { note ->
                    Log.d("TaskDetails", "---- Note Details ----")
                    Log.d("TaskDetails", "Notes: ${note.notes.joinToString(", ")}")
                }
            }
        }
    }

    fun calculateTotalDistance(): String {
        return gameCalculations.calculateTotalDistance(_tasks.value)
    }
}

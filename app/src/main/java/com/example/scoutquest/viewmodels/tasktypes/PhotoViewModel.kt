package com.example.scoutquest.viewmodels.tasktypes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutquest.data.models.tasktypes.Photo
import com.example.scoutquest.viewmodels.general.CreateNewGameViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor() : ViewModel() {
    private lateinit var createNewGameViewModel: CreateNewGameViewModel

    private val _instruction = MutableStateFlow<String>("")
    val instruction: StateFlow<String> = _instruction

    private val _taskDetailsEntered = MutableStateFlow(false)
    val hasInstruction: StateFlow<Boolean> = _instruction.map { it.isNotBlank() }.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val taskDetailsEntered: StateFlow<Boolean> = _taskDetailsEntered

    fun setCreateNewGameViewModel(viewModel: CreateNewGameViewModel) {
        createNewGameViewModel = viewModel
    }

    fun setInstruction(instruction: String) {
        _instruction.value = instruction
        _taskDetailsEntered.value = true
    }

    fun getCurrentPhotoTask(): Photo {
        return Photo(
            instruction = _instruction.value
        )
    }

    fun setInstructionFromPhoto(photo: Photo?) {
        _instruction.value = photo?.instruction ?: ""
        _taskDetailsEntered.value = _instruction.value.isNotBlank()
    }

    fun saveCurrentPhotoTask() {
        val currentPhotoTask = getCurrentPhotoTask()
        createNewGameViewModel.currentPhotoTaskDetails = currentPhotoTask
    }

    fun resetPhotoTask() {
        _instruction.value = ""
        _taskDetailsEntered.value = false
    }

    fun setTaskDetailsEntered(entered: Boolean) {
        _taskDetailsEntered.value = entered
    }
}

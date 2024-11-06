package com.example.scoutquest.viewmodels.tasktypes

import androidx.lifecycle.ViewModel
import com.example.scoutquest.data.models.tasktypes.Photo
import com.example.scoutquest.viewmodels.general.CreateNewGameViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor() : ViewModel() {
    private lateinit var createNewGameViewModel: CreateNewGameViewModel

    private val _instruction = MutableStateFlow("")
    val instruction: StateFlow<String> = _instruction

    private val _hasInstruction = MutableStateFlow(false)
    val hasInstruction: StateFlow<Boolean> = _hasInstruction

    fun setCreateNewGameViewModel(viewModel: CreateNewGameViewModel) {
        createNewGameViewModel = viewModel
    }

    fun setInstruction(newInstruction: String) {
        _instruction.value = newInstruction
        _hasInstruction.value = newInstruction.isNotBlank()
        setTaskDetailsEntered(newInstruction.isNotBlank())
    }

    fun setInstructionFromPhoto(photo: Photo?) {
        _instruction.value = photo?.description ?: ""
        _hasInstruction.value = _instruction.value.isNotBlank()
        setTaskDetailsEntered(_instruction.value.isNotBlank())
    }

    fun saveCurrentPhotoTask() {
        val currentPhoto = getCurrentPhotoTask()
        createNewGameViewModel.currentPhotoTaskDetails = currentPhoto
    }

    fun getCurrentPhotoTask(): Photo {
        return Photo(description = _instruction.value)
    }

    fun resetPhotoTask() {
        _instruction.value = ""
        _hasInstruction.value = false
        setTaskDetailsEntered(false)
    }

    fun setTaskDetailsEntered(entered: Boolean) {
        createNewGameViewModel.setTaskDetailsEntered(entered)
    }
}


package com.example.scoutquest.viewmodels.tasktypes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.scoutquest.data.models.tasktypes.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _noteText = MutableStateFlow("")
    val noteText: StateFlow<String> get() = _noteText

    fun updateNoteText(newText: String) {
        _noteText.value = newText
    }

    fun clearNoteText() {
        _noteText.value = ""
    }

    fun resetNote() {
        clearNoteText()
    }

    fun getCurrentNote(): Note {
        return Note(_noteText.value)
    }
}

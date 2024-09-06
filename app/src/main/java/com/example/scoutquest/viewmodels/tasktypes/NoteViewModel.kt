package com.example.scoutquest.viewmodels.tasktypes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NoteViewModel : ViewModel() {
    private val _noteText = MutableStateFlow("")
    val noteText: StateFlow<String> get() = _noteText

    fun updateNoteText(newText: String) {
        _noteText.value = newText
    }

    fun clearNoteText() {
        _noteText.value = ""
    }
}

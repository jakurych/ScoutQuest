package com.example.scoutquest.viewmodels.tasktypes

import androidx.lifecycle.ViewModel
import com.example.scoutquest.data.models.tasktypes.Note
import com.example.scoutquest.viewmodels.general.CreateNewGameViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class NoteViewModel @Inject constructor() : ViewModel() {

    private val _notes = MutableStateFlow<List<String>>(emptyList())
    val notes: StateFlow<List<String>> = _notes

    private val _hasNotes = MutableStateFlow(false)
    val hasNotes: StateFlow<Boolean> = _hasNotes

    fun setNotesFromNote(note: Note?) {
        _notes.value = note?.notes ?: emptyList()
        _hasNotes.value = _notes.value.isNotEmpty()
    }

    fun addNote(note: String) {
        _notes.update { currentNotes ->
            val newNotes = currentNotes + note
            _hasNotes.value = newNotes.isNotEmpty()
            newNotes
        }
    }

    fun removeNote(index: Int) {
        _notes.update { currentNotes ->
            if (index in currentNotes.indices) {
                val newNotes = currentNotes.toMutableList().apply { removeAt(index) }
                _hasNotes.value = newNotes.isNotEmpty()
                newNotes
            } else {
                currentNotes
            }
        }
    }

    fun getCurrentNote(): Note {
        return Note(notes = _notes.value)
    }

    fun resetNote() {
        _notes.value = emptyList()
        _hasNotes.value = false
    }

}

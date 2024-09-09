package com.example.scoutquest.viewmodels.tasktypes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.scoutquest.data.models.tasktypes.Note
import com.example.scoutquest.viewmodels.CreateNewGameViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor() : ViewModel() {
    private lateinit var createNewGameViewModel: CreateNewGameViewModel

    private val _notes = MutableStateFlow<List<String>>(emptyList())
    val notes: StateFlow<List<String>> = _notes

    private val _hasNotes = MutableStateFlow(false)
    val hasNotes: StateFlow<Boolean> = _hasNotes

    fun setCreateNewGameViewModel(viewModel: CreateNewGameViewModel) {
        createNewGameViewModel = viewModel
    }

    fun addNote(note: String) {
        _notes.update { currentNotes ->
            val newNotes = currentNotes + note
            _hasNotes.value = newNotes.isNotEmpty()
            setTaskDetailsEntered(newNotes.isNotEmpty())
            newNotes
        }
    }

    fun removeNote(index: Int) {
        _notes.update { currentNotes ->
            if (index in currentNotes.indices) {
                val newNotes = currentNotes.toMutableList().apply { removeAt(index) }
                _hasNotes.value = newNotes.isNotEmpty()
                setTaskDetailsEntered(newNotes.isNotEmpty())
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
        setTaskDetailsEntered(false)
    }

    fun isNoteEntered(): Boolean {
        return _notes.value.isNotEmpty()
    }

    fun setTaskDetailsEntered(entered: Boolean) {
        createNewGameViewModel.setTaskDetailsEntered(entered)
    }
}


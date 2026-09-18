package com.joybrata.notepad.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joybrata.notepad.domain.model.Note
import com.joybrata.notepad.domain.usecase.InvalidNoteException
import com.joybrata.notepad.domain.usecase.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Joybrata Paul on 10/08/2024
 **/
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    val uiState: StateFlow<NotesUiState> = noteUseCases.getNotes()
        .onEach { /* no-op: mapping handled below */ }
        .let { notesFlow ->
            MutableStateFlow(NotesUiState()).also { state ->
                notesFlow
                    .onEach { notes ->
                        state.value = NotesUiState(notes = notes, isLoading = false)
                    }
                    .launchIn(viewModelScope)
            }
        }
        .asStateFlow()

    private val _events = Channel<NotesUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun addNote(content: String) {
        viewModelScope.launch {
            try {
                noteUseCases.addNote(content)
            } catch (e: InvalidNoteException) {
                _events.send(NotesUiEvent.ShowMessage(e.message ?: "Invalid note"))
            }
        }
    }

    fun updateNote(note: Note, newContent: String) {
        viewModelScope.launch {
            try {
                noteUseCases.updateNote(note, newContent)
            } catch (e: InvalidNoteException) {
                _events.send(NotesUiEvent.ShowMessage(e.message ?: "Invalid note"))
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteUseCases.deleteNote(note)
            _events.send(NotesUiEvent.NoteDeleted(note))
        }
    }

    /** Re-inserts a previously deleted note (used by the undo action). */
    fun restoreNote(note: Note) {
        viewModelScope.launch {
            noteUseCases.addNote(note.content)
        }
    }
}

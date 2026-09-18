package com.joybrata.notepad.presentation

import com.joybrata.notepad.domain.model.Note

/**
 * Created by Joybrata Paul on 10/08/2024
 **/

/**
 * Immutable UI state for the notes screen, exposed via StateFlow.
 */
data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * One-off UI events (messages) that should be shown once and not survive
 * configuration changes as state.
 */
sealed interface NotesUiEvent {
    data class ShowMessage(val message: String) : NotesUiEvent
    data class NoteDeleted(val note: Note) : NotesUiEvent
}

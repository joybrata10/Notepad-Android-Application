package com.joybrata.notepad.domain.usecase

import com.joybrata.notepad.domain.model.Note
import com.joybrata.notepad.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Joybrata Paul on 10/08/2024
 **/

/**
 * Groups all note-related use cases so a ViewModel can depend on a single
 * injected object rather than many individual use cases.
 */
data class NoteUseCases @Inject constructor(
    val getNotes: GetNotesUseCase,
    val addNote: AddNoteUseCase,
    val updateNote: UpdateNoteUseCase,
    val deleteNote: DeleteNoteUseCase
)

class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> = repository.getNotes()
}

class AddNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    /**
     * @throws InvalidNoteException if the note content is blank.
     */
    suspend operator fun invoke(content: String): Long {
        val trimmed = content.trim()
        if (trimmed.isBlank()) {
            throw InvalidNoteException("Note content cannot be empty.")
        }
        return repository.addNote(Note(content = trimmed))
    }
}

class UpdateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note, newContent: String) {
        val trimmed = newContent.trim()
        if (trimmed.isBlank()) {
            throw InvalidNoteException("Note content cannot be empty.")
        }
        repository.updateNote(note.copy(content = trimmed))
    }
}

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) = repository.deleteNote(note)
}

/** Thrown when a note fails domain validation. */
class InvalidNoteException(message: String) : Exception(message)

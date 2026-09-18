package com.joybrata.notepad.domain.repository

import com.joybrata.notepad.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Created by Joybrata Paul on 10/08/2024
 **/

/**
 * Abstraction over the note data source. The domain layer depends only on this
 * interface; the concrete implementation lives in the data layer.
 */
interface NoteRepository {
    fun getNotes(): Flow<List<Note>>
    suspend fun addNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun getNoteById(id: Long): Note?
}

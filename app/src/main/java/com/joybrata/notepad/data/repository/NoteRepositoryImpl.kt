package com.joybrata.notepad.data.repository

import com.joybrata.notepad.data.local.dao.NoteDao
import com.joybrata.notepad.data.mapper.toDomain
import com.joybrata.notepad.data.mapper.toEntity
import com.joybrata.notepad.domain.model.Note
import com.joybrata.notepad.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Joybrata Paul on 10/08/2024
 **/

/**
 * Concrete [NoteRepository] backed by Room. Translates between entities and
 * domain models so the rest of the app never sees persistence types.
 */
@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao
) : NoteRepository {

    override fun getNotes(): Flow<List<Note>> =
        dao.getNotes().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addNote(note: Note): Long = dao.insertNote(note.toEntity())

    override suspend fun updateNote(note: Note) = dao.updateNote(note.toEntity())

    override suspend fun deleteNote(note: Note) = dao.deleteNote(note.toEntity())

    override suspend fun getNoteById(id: Long): Note? = dao.getNoteById(id)?.toDomain()
}

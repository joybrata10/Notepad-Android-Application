package com.joybrata.notepad.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.joybrata.notepad.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Joybrata Paul on 10/08/2024
 **/
@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY createdAt ASC")
    fun getNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)
}

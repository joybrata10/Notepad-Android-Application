package com.joybrata.notepad.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joybrata.notepad.data.local.dao.NoteDao
import com.joybrata.notepad.data.local.entity.NoteEntity

/**
 * Created by Joybrata Paul on 10/08/2024
 **/
@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "notes.db"
    }
}

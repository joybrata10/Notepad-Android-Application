package com.joybrata.notepad.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Joybrata Paul on 10/08/2024
 **/

/**
 * Room persistence model for a note. Lives entirely in the data layer.
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

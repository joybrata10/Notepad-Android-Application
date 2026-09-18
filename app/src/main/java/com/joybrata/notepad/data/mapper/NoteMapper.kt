package com.joybrata.notepad.data.mapper

import com.joybrata.notepad.data.local.entity.NoteEntity
import com.joybrata.notepad.domain.model.Note

/**
 * Created by Joybrata Paul on 10/08/2024
 **/

/** Maps between the Room entity and the domain model. */

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    content = content,
    createdAt = createdAt
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    content = content,
    createdAt = createdAt
)

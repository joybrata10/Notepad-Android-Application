package com.joybrata.notepad.domain.model

/**
 * Created by Joybrata Paul on 10/08/2024
 **/

/**
 * Core domain model representing a single note.
 * Kept free of any framework/persistence annotations so the domain layer
 * has no dependency on Android or Room.
 */
data class Note(
    val id: Long = 0,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

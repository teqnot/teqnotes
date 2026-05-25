package com.example.teqnotes.features.home.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String = "",
    val projectId: String? = null, // null - single note
    val timestamp: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
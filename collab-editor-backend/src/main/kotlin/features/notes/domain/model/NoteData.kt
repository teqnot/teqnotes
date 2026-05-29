package com.example.features.notes.domain.model

import java.time.LocalDateTime

data class NoteData(
    val id: Int,
    val title: String,
    val content: String?,
    val ownerId: Int,
    val projectId: Int?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class NoteAccessData(
    val noteId: Int,
    val userId: Int,
    val role: String,
    val grantedBy: Int?,
    val grantedAt: LocalDateTime
)
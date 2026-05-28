package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequest(
    val title: String,
    val content: String? = null,
    val projectId: Int? = null
)

@Serializable
data class UpdateNoteRequest(
    val title: String? = null,
    val content: String? = null
)

@Serializable
data class NoteResponse(
    val id: Int,
    val title: String,
    val content: String?,
    val projectId: Int?,
    val ownerId: Int,
    val accessRole: String
)
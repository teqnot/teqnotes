package com.example.teqnotes.features.sharing.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ShareNoteRequest(
    val targetUserId: Int,
    val role: String
)

@Serializable
data class AddMemberRequest(
    val email: String,
    val role: String
)
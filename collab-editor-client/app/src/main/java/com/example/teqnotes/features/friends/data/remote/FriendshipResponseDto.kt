package com.example.teqnotes.features.friends.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class FriendshipResponseDto(
    val id: Int,
    val userId: Int,
    val name: String,
    val email: String,
    val status: String
)
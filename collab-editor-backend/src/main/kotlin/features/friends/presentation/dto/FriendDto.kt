package com.example.features.friends.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val friendEmail: String
)

@Serializable
data class FriendshipResponse(
    val id: Int,
    val userId: Int,
    val name: String,
    val email: String,
    val status: String // "accepted", "pending"
)
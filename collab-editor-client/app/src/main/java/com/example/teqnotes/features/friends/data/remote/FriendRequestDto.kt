package com.example.teqnotes.features.friends.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestDto(
    val friendEmail: String
)
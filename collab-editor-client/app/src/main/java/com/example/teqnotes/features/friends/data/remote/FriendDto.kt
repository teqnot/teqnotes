package com.example.teqnotes.features.friends.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendDto(
    @SerialName("userId")
    val id: Int,
    val name: String,
    val email: String,
    val status: String? = null
)
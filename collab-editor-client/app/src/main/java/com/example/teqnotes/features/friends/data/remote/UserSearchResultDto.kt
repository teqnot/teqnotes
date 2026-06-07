package com.example.teqnotes.features.friends.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class UserSearchResultDto(
    val id: Int,
    val name: String,
    val email: String
)

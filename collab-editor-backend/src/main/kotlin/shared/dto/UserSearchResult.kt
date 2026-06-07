package com.example.shared.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserSearchResult(
    val id: Int,
    val name: String,
    val email: String
)
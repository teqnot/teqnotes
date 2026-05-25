package com.example.teqnotes.features.friends.domain.model

data class Friend(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
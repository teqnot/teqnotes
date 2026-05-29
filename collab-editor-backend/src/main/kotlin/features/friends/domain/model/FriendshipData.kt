package com.example.features.friends.domain.model

import java.time.LocalDateTime

data class FriendshipData(
    val id: Int,
    val userId1: Int,
    val userId2: Int,
    val status: String,
    val createdAt: LocalDateTime
)
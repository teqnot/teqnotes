package com.example.features.auth.domain.model

import java.time.LocalDateTime

data class UserData(
    val id: Int,
    val email: String,
    val name: String,
    val passwordHash: String, // Нужно для логина
    val createdAt: LocalDateTime
)
package com.example.features.auth.domain.model

import java.time.LocalDateTime

data class RefreshTokenData(
    val id: Int,
    val userId: Int,
    val token: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime
)
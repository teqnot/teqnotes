package com.example.teqnotes.features.auth.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RefreshRequest(
    val refreshToken: String
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserInfoDto
)

@Serializable
data class UserInfoDto(
    val id: Int,
    val email: String,
    val name: String
)

@Serializable
data class ErrorResponse(
    val error: String
)
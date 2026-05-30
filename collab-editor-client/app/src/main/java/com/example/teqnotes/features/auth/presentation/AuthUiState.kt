package com.example.teqnotes.features.auth.presentation

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: com.example.teqnotes.features.auth.domain.model.User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

data class AuthUiEvent(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val type: Type
) {
    enum class Type { LOGIN, REGISTER, LOGOUT }
}
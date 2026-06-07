package com.example.teqnotes.features.auth.domain.repository

import com.example.teqnotes.core.storage.TokenStorage
import com.example.teqnotes.features.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(email: String, password: String, name: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun refreshTokens(): Result<User>
    suspend fun logout()
    suspend fun deleteAccount(): Result<Unit>

    fun getCurrentUser(): Flow<TokenStorage.UserInfo?>
    fun isLoggedIn(): Flow<Boolean>
}
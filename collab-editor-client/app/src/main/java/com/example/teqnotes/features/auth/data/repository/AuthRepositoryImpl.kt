package com.example.teqnotes.features.auth.data.repository

import com.example.teqnotes.core.storage.TokenStorage
import com.example.teqnotes.features.auth.data.remote.AuthApi
import com.example.teqnotes.features.auth.data.remote.LoginRequest
import com.example.teqnotes.features.auth.data.remote.RefreshRequest
import com.example.teqnotes.features.auth.data.remote.RegisterRequest
import com.example.teqnotes.features.auth.data.remote.UserApi
import com.example.teqnotes.features.auth.data.remote.UserInfoDto
import com.example.teqnotes.features.auth.domain.model.User
import com.example.teqnotes.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val userApi: UserApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun register(email: String, password: String, name: String): Result<User> {
        return authApi.register(RegisterRequest(email, password, name))
            .mapCatching { response ->
                tokenStorage.saveTokens(response.accessToken, response.refreshToken)
                tokenStorage.saveUserInfo(
                    response.user.id,
                    response.user.email,
                    response.user.name
                )
                response.user.toDomain()
            }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return authApi.login(LoginRequest(email, password))
            .mapCatching { response ->
                tokenStorage.saveTokens(response.accessToken, response.refreshToken)
                tokenStorage.saveUserInfo(
                    response.user.id,
                    response.user.email,
                    response.user.name
                )
                response.user.toDomain()
            }
    }

    override suspend fun refreshTokens(): Result<User> {
        val refreshToken = tokenStorage.getRefreshToken()
            ?: return Result.failure(Exception("No refresh token found. Please login again."))

        return authApi.refresh(RefreshRequest(refreshToken))
            .mapCatching { response ->
                tokenStorage.saveTokens(response.accessToken, response.refreshToken)
                response.user.toDomain()
            }
    }

    override suspend fun logout() {
        tokenStorage.clear()
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return userApi.deleteAccount()
            .onSuccess { tokenStorage.clear() }
    }

    override fun getCurrentUser(): Flow<TokenStorage.UserInfo?> = tokenStorage.getCurrentUser()

    override fun isLoggedIn(): Flow<Boolean> = tokenStorage.isLoggedIn()
}

private fun UserInfoDto.toDomain(): User = User(
    id = id,
    email = email,
    name = name
)
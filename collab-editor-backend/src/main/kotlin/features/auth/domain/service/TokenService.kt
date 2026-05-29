package com.example.features.auth.domain.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.example.config.SecurityConfig
import com.example.features.auth.data.repository.RefreshTokenRepository
import com.example.features.auth.data.repository.UserRepository
import com.example.features.auth.data.model.User
import com.example.features.auth.domain.model.UserData
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

class TokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository
) {
    private val REFRESH_TOKEN_EXPIRY_DAYS = 7

    fun generateTokenPair(user: UserData): Pair<String, String> {
        val accessToken = generateAccessToken(user)
        val refreshToken = generateRefreshToken(user.id)
        return Pair(accessToken, refreshToken)
    }

    fun refreshAccessToken(refreshToken: String): Result<Pair<UserData, Pair<String, String>>> {
        return try {
            val tokenData = refreshTokenRepository.findByToken(refreshToken)
                ?: throw IllegalArgumentException("Invalid refresh token")

            if (tokenData.expiresAt.isBefore(LocalDateTime.now())) {
                refreshTokenRepository.deleteByToken(refreshToken)
                throw IllegalArgumentException("Refresh token expired")
            }

            val userData = userRepository.findById(tokenData.userId)
                ?: throw IllegalArgumentException("User not found")

            val newAccessToken = generateAccessToken(userData)
            val newRefreshToken = generateRefreshToken(userData.id)

            refreshTokenRepository.deleteByToken(refreshToken)

            Result.success(Pair(userData, Pair(newAccessToken, newRefreshToken)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun generateAccessToken(user: UserData): String {
        return JWT.create()
            .withAudience(SecurityConfig.AUDIENCE)
            .withIssuer(SecurityConfig.ISSUER)
            .withSubject(user.id.toString()) // user.id уже Int
            .withClaim("email", user.email)
            .withClaim("name", user.name)
            .withExpiresAt(Date(System.currentTimeMillis() + SecurityConfig.EXPIRES_IN.toMillis()))
            .sign(SecurityConfig.algorithm)
    }

    fun generateRefreshToken(userId: Int): String {
        val tokenValue = UUID.randomUUID().toString()
        val expiresAt = LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS.toLong())

        refreshTokenRepository.deleteByUserId(userId)
        refreshTokenRepository.create(userId, tokenValue, expiresAt)

        return tokenValue
    }

    fun validateAccessToken(token: String): Result<UserData> {
        return try {
            val verifier = JWT
                .require(SecurityConfig.algorithm)
                .withAudience(SecurityConfig.AUDIENCE)
                .withIssuer(SecurityConfig.ISSUER)
                .build()

            val jwt = verifier.verify(token)
            val userId = jwt.subject.toInt()

            val user = userRepository.findById(userId)
                ?: throw IllegalArgumentException("User not found")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
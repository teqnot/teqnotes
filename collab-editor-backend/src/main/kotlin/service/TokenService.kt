package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.example.config.SecurityConfig
import com.example.model.User
import com.example.repository.RefreshTokenRepository
import com.example.repository.UserRepository
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

class TokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository
) {
    private val REFRESH_TOKEN_EXPIRY_DAYS = 7

    fun generateTokenPair(user: User): Pair<String, String> {
        val accessToken = generateAccessToken(user)
        val refreshToken = generateRefreshToken(user.id.value)
        return Pair(accessToken, refreshToken)
    }

    fun refreshAccessToken(refreshToken: String): Result<Pair<String, String>> {
        return try {
            val tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                ?: throw IllegalArgumentException("Invalid refresh token")

            if (tokenEntity.expiresAt.isBefore(LocalDateTime.now())) {
                refreshTokenRepository.deleteByToken(refreshToken)
                throw IllegalArgumentException("Refresh token expired")
            }

            val user = tokenEntity.user

            val newAccessToken = generateAccessToken(user)
            val newRefreshToken = generateRefreshToken(user.id.value)

            refreshTokenRepository.deleteByToken(refreshToken)

            Result.success(Pair(newAccessToken, newRefreshToken))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun generateAccessToken(user: User): String {
        return JWT.create()
            .withAudience(SecurityConfig.AUDIENCE)
            .withIssuer(SecurityConfig.ISSUER)
            .withSubject(user.id.value.toString())
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

    fun validateAccessToken(token: String): Result<User> {
        return try {
            val verifier: JWTVerifier = JWT
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
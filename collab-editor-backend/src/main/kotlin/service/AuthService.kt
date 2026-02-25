package com.example.service

import com.auth0.jwt.JWT
import com.example.config.SecurityConfig
import com.example.model.RefreshToken
import com.example.model.RefreshTokens
import com.example.model.User
import com.example.model.Users
import com.example.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

class AuthService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService
) {

    fun register(email: String, password: String, name: String): Result<Pair<User, Pair<String, String>>> {
        return try {
            if (userRepository.existsByEmail(email)) {
                throw IllegalArgumentException("User with this email already exists")
            }

            val passwordHash = hashPassword(password)

            val user = userRepository.create(email, passwordHash, name)

            val (accessToken, refreshToken) = tokenService.generateTokenPair(user)

            println("User registered: ${user.email}")
            Result.success(Pair(user, Pair(accessToken, refreshToken)))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun authenticate(email: String, password: String): Result<Pair<String, String>> {
        return try {
            val user = userRepository.findByEmail(email)
                ?: throw IllegalArgumentException("Invalid credentials")

            if (!verifyPassword(password, user.passwordHash)) {
                throw IllegalArgumentException("Invalid credentials")
            }

            val (accessToken, refreshToken) = tokenService.generateTokenPair(user)

            Result.success(Pair(accessToken, refreshToken))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    private fun verifyPassword(password: String, hash: String): Boolean {
        return BCrypt.checkpw(password, hash)
    }
}
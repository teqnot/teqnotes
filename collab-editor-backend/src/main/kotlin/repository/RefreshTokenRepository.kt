package com.example.repository

import com.example.model.RefreshToken
import com.example.model.RefreshTokens
import com.example.model.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

interface RefreshTokenRepository {
    fun findByToken(token: String): RefreshToken?
    fun create(userId: Int, token: String, expiresAt: LocalDateTime): RefreshToken
    fun deleteByToken(token: String): Boolean
    fun deleteByUserId(userId: Int): Int
    fun deleteExpired(): Int
}

object RefreshTokenRepositoryImpl : RefreshTokenRepository {
    override fun findByToken(token: String): RefreshToken?  = transaction {
        RefreshToken.find { RefreshTokens.token eq token }.firstOrNull()
    }

    override fun create(userId: Int, token: String, expiresAt: LocalDateTime): RefreshToken = transaction {
        RefreshToken.new {
            this.token = token
            this.user = User[userId]
            this.expiresAt = expiresAt
        }
    }

    override fun deleteByToken(token: String): Boolean = transaction {
        val refreshToken = RefreshToken.find { RefreshTokens.token eq token }.firstOrNull()
        refreshToken?.delete()
        refreshToken != null
    }

    override fun deleteByUserId(userId: Int): Int = transaction {
        RefreshToken.find { RefreshTokens.userId eq userId }
            .also { tokens -> tokens.forEach { it.delete() } }
            .count()
            .toInt()
    }

    override fun deleteExpired(): Int = transaction {
        val now = LocalDateTime.now()
        RefreshToken.find { RefreshTokens.expiresAt less now }
            .also { tokens -> tokens.forEach { it.delete() } }
            .count()
            .toInt()
    }
}
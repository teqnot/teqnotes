package com.example.features.auth.data.repository

import com.example.features.auth.data.model.RefreshToken
import com.example.features.auth.data.model.RefreshTokens
import com.example.features.auth.data.model.User
import com.example.features.auth.domain.model.RefreshTokenData
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

interface RefreshTokenRepository {
    fun findByToken(token: String): RefreshTokenData?
    fun create(userId: Int, token: String, expiresAt: LocalDateTime): RefreshTokenData
    fun deleteByToken(token: String): Boolean
    fun deleteByUserId(userId: Int): Int
    fun deleteExpired(): Int
}

object RefreshTokenRepositoryImpl : RefreshTokenRepository {

    override fun findByToken(token: String): RefreshTokenData? = transaction {
        RefreshToken.find { RefreshTokens.token eq token }.firstOrNull()?.let { toData(it) }
    }

    override fun create(userId: Int, token: String, expiresAt: LocalDateTime): RefreshTokenData = transaction {
        val entity = RefreshToken.new {
            this.token = token
            this.user = User[userId]
            this.expiresAt = expiresAt
        }
        toData(entity)
    }

    override fun deleteByToken(token: String): Boolean = transaction {
        val entity = RefreshToken.find { RefreshTokens.token eq token }.firstOrNull()
        entity?.delete()
        entity != null
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

    private fun toData(entity: RefreshToken): RefreshTokenData = RefreshTokenData(
        id = entity.id.value,
        userId = entity.user.id.value,
        token = entity.token,
        expiresAt = entity.expiresAt,
        createdAt = entity.createdAt
    )
}
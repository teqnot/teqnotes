package com.example.features.auth.data.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object RefreshTokens: IntIdTable("refresh_tokens") {
    val token = varchar("token", 255).uniqueIndex()
    val userId = reference("user_id", Users)
    val expiresAt = datetime("expires_at")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
}

class RefreshToken(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RefreshToken>(RefreshTokens)

    var token by RefreshTokens.token
    var user by User.Companion referencedOn RefreshTokens.userId
    var expiresAt by RefreshTokens.expiresAt
    var createdAt by RefreshTokens.createdAt
}
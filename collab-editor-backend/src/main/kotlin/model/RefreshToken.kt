package com.example.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime

object RefreshTokens: IntIdTable() {
    val token = varchar("token", 255).uniqueIndex()
    val userId = reference("user_id", Users)
    val expiresAt = datetime("expires_at")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}

class RefreshToken(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RefreshToken>(RefreshTokens)

    var token by RefreshTokens.token
    var user by User referencedOn RefreshTokens.userId
    var expiresAt by RefreshTokens.expiresAt
    var createdAt by RefreshTokens.createdAt
}
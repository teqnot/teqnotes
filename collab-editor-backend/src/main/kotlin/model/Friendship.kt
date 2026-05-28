package com.example.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime


object Friendships : IntIdTable() {
    val userId1 = reference("user_id_1", Users, onDelete = ReferenceOption.CASCADE)
    val userId2 = reference("user_id_2", Users, onDelete = ReferenceOption.CASCADE)
    val status = varchar("status", 20).clientDefault { "pending" }
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    init {
        uniqueIndex(userId1, userId2)
    }
}

class Friendship(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Friendship>(Friendships)

    var user1 by User referencedOn Friendships.userId1
    var user2 by User referencedOn Friendships.userId2
    var status by Friendships.status
    var createdAt by Friendships.createdAt
}
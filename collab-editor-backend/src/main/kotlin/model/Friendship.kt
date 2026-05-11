package com.example.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime

object Friendships : IntIdTable() {
    val userId1 = reference("user_id_1", Users, onDelete = ReferenceOption.CASCADE)
    val userId2 = reference("user_id_2", Users, onDelete = ReferenceOption.CASCADE)
    val status = varchar("status", 20).clientDefault { "pending" } // "pending", "accepted", "rejected"
    val createdAt = datetime("created_at").defaultExpression(CurrentTimestamp())
}

class Friendship(id: org.jetbrains.exposed.dao.id.EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Friendship>(Friendships)

    var userId1 by Users.id
    var userId2 by Users.id
    var status by Friendships.status
    var createdAt by Friendships.createdAt
}
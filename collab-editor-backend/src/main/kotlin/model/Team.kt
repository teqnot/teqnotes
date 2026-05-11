package com.example.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime

object Teams : IntIdTable() {
    val name = varchar("name", 255)
    val ownerId = reference("owner_id", Users)
    val createdAt = datetime("created_at").defaultExpression(CurrentTimestamp())
}

class Team(id: org.jetbrains.exposed.dao.id.EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Team>(Teams)

    var name by Teams.name
    var ownerId by Users.id
    var createdAt by Teams.createdAt
}
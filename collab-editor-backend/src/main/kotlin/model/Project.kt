package com.example.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime

object Projects : IntIdTable() {
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val teamId = reference("team_id", Teams, onDelete = ReferenceOption.SET_NULL).nullable()
    val ownerId = reference("owner_id", Users)
    val createdAt = datetime("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = datetime("updated_at").defaultExpression(CurrentTimestamp())
}

class Project(id: org.jetbrains.exposed.dao.id.EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Project>(Projects)

    var name by Projects.name
    var description by Projects.description
    var teamId by Projects.teamId
    var ownerId by Users.id
    var createdAt by Projects.createdAt
    var updatedAt by Projects.updatedAt
}
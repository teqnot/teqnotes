package com.example.features.notes.data.local

import com.example.features.auth.data.model.User
import com.example.features.auth.data.model.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Projects : IntIdTable() {
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val ownerId = reference("owner_id", Users, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
}

class Project(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Project>(Projects)

    var name by Projects.name
    var description by Projects.description

    var owner by User referencedOn Projects.ownerId

    var createdAt by Projects.createdAt
    var updatedAt by Projects.updatedAt
}
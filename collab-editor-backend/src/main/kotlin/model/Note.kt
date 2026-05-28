package com.example.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime

object Notes : IntIdTable() {
    val title = varchar("title", 255)
    val content = text("content").nullable()

    val ownerId = reference("owner_id", Users, onDelete = ReferenceOption.CASCADE)
    val projectId = reference("project_id", Projects, onDelete = ReferenceOption.SET_NULL).nullable()

    val versionVectorJson = text("version_vector").nullable()

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}

class Note(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Note>(Notes)

    var title by Notes.title
    var content by Notes.content

    var owner by User referencedOn Notes.ownerId
    var project by Project optionalReferencedOn Notes.projectId

    var versionVectorJson by Notes.versionVectorJson
    var createdAt by Notes.createdAt
    var updatedAt by Notes.updatedAt
}
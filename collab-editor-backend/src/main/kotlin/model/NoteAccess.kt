package com.example.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime

object NoteAccesses : IntIdTable() {
    val noteId = reference("note_id", Notes, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 20).clientDefault { "viewer" }
    val grantedBy = reference("granted_by", Users, onDelete = ReferenceOption.SET_NULL)
    val grantedAt = datetime("granted_at").defaultExpression(CurrentTimestamp())
}

class NoteAccess(id: org.jetbrains.exposed.dao.id.EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NoteAccess>(NoteAccesses)

    var noteId by Notes.id
    var userId by Users.id
    var role by NoteAccesses.role
    var grantedBy by Users.id
    var grantedAt by NoteAccesses.grantedAt
}
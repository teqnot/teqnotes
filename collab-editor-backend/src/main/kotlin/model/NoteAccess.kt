package com.example.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime

enum class NoteAccessRole {
    READ_ONLY,
    READ_WRITE
}

object NoteAccesses : IntIdTable() {
    val noteId = reference("note_id", Notes, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)

    val role = varchar("role", 20).clientDefault { NoteAccessRole.READ_ONLY.name }

    val grantedBy = reference("granted_by", Users, onDelete = ReferenceOption.SET_NULL).nullable()
    val grantedAt = datetime("granted_at").defaultExpression(CurrentDateTime)

    init {
        uniqueIndex(noteId, userId)
    }
}

class NoteAccess(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NoteAccess>(NoteAccesses)

    var note by Note referencedOn NoteAccesses.noteId
    var user by User referencedOn NoteAccesses.userId
    var role by NoteAccesses.role
    var grantedBy by NoteAccesses.grantedBy
    var grantedAt by NoteAccesses.grantedAt

    fun getRoleEnum(): NoteAccessRole = try {
        NoteAccessRole.valueOf(role)
    } catch (e: IllegalArgumentException) {
        NoteAccessRole.READ_ONLY
    }
}
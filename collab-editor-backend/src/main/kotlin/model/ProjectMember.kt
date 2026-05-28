package com.example.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

enum class ProjectRole {
    OWNER,
    EDITOR,
    VIEWER
}

object ProjectMembers : IntIdTable() {
    val projectId = reference("project_id", Projects, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 20).clientDefault { ProjectRole.VIEWER.name }
    val joinedAt = datetime("joined_at").defaultExpression(CurrentDateTime)

    init {
        uniqueIndex(projectId, userId)
    }
}

class ProjectMember(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ProjectMember>(ProjectMembers)

    var project by Project referencedOn ProjectMembers.projectId
    var user by User referencedOn ProjectMembers.userId

    var role by ProjectMembers.role
    var joinedAt by ProjectMembers.joinedAt

    fun getRoleEnum(): ProjectRole = try {
        ProjectRole.valueOf(role)
    } catch (e: IllegalArgumentException) {
        ProjectRole.VIEWER
    }
}
package com.example.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime

object ProjectMembers : IntIdTable() {
    val projectId = reference("project_id", Projects, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 20).clientDefault { "viewer" } // "owner", "editor", "viewer"
    val accessLevel = varchar("access_level", 20).clientDefault { "read" } // "read", "read_write"
    val joinedAt = datetime("joined_at").defaultExpression(CurrentTimestamp())
}

class ProjectMember(id: org.jetbrains.exposed.dao.id.EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ProjectMember>(ProjectMembers)

    var projectId by Projects.id
    var userId by Users.id
    var role by ProjectMembers.role
    var accessLevel by ProjectMembers.accessLevel
    var joinedAt by ProjectMembers.joinedAt
}
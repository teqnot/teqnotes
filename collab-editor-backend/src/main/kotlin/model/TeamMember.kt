package com.example.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime

object TeamMembers : IntIdTable() {
    val teamId = reference("team_id", Teams, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 20).clientDefault { "member" } // "admin", "member"
    val joinedAt = datetime("joined_at").defaultExpression(CurrentTimestamp())
}

class TeamMember(id: org.jetbrains.exposed.dao.id.EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TeamMember>(TeamMembers)

    var teamId by Teams.id
    var userId by Users.id
    var role by TeamMembers.role
    var joinedAt by TeamMembers.joinedAt
}
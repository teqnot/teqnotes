package com.example.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime

private val gson = Gson()
private val mapType = object : TypeToken<Map<String, Int>>() {}.type

object Notes : IntIdTable() {
    val title = varchar("title", 255)
    val ownerId = reference("owner_id", Users)
    val projectId = reference("project_id", Projects, onDelete = ReferenceOption.SET_NULL).nullable()
    val teamId = reference("team_id", Teams, onDelete = ReferenceOption.SET_NULL).nullable()
    val versionVectorJson = text("version_vector")
    val createdAt = datetime("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = datetime("updated_at").defaultExpression(CurrentTimestamp())
}

class Note(id: org.jetbrains.exposed.dao.id.EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Note> (Notes)

    var title by Notes.title
    var ownerId by Users.id
    var projectId by Projects.id
    var teamId by Teams.id

    private var _versionVectorJson by Notes.versionVectorJson
    var versionVector: Map<String, Int>
        get() = gson.fromJson(_versionVectorJson, mapType) ?: emptyMap()
        set(value) {
            _versionVectorJson = gson.toJson(value)
        }

    var createdAt by Notes.createdAt
    var updatedAt by Notes.updatedAt
}

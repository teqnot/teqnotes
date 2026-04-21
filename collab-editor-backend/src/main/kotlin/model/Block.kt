package com.example.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.UUID

object Blocks : UUIDTable() {
    val noteId = reference("note_id", Notes)
    val type = varchar("type", 20).clientDefault { BlockType.PARAGRAPH.name }
    val content = text("content")
    val order = integer("order")
    val deleted = bool("deleted").default(false)
    val checked = bool("checked").default(false)
    val createdAt = datetime("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = datetime("updated_at").defaultExpression(CurrentTimestamp())
}

class Block(id: org.jetbrains.exposed.dao.id.EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Block>(Blocks)

    var noteId by Notes.id
    var type by Blocks.type
    var content by Blocks.content
    var order by Blocks.order
    var deleted by Blocks.deleted
    var checked by Blocks.checked
    var createdAt by Blocks.createdAt
    var updatedAt by Blocks.updatedAt
}
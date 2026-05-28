package com.example.model

data class ProjectData(
    val id: Int,
    val name: String,
    val description: String?,
    val ownerId: Int,
    val createdAt: java.time.LocalDateTime,
    val updatedAt: java.time.LocalDateTime
)

data class ProjectMemberData(
    val projectId: Int,
    val userId: Int,
    val role: String
)
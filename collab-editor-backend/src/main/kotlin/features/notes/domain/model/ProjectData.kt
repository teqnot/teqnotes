package com.example.features.notes.domain.model

import java.time.LocalDateTime

data class ProjectData(
    val id: Int,
    val name: String,
    val description: String?,
    val ownerId: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class ProjectMemberData(
    val projectId: Int,
    val userId: Int,
    val role: String
)
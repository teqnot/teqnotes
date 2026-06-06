package com.example.teqnotes.features.home.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ProjectDto(
    val id: Int,
    val name: String,
    val description: String?,
    val ownerId: Int,
    val role: String
)

@Serializable
data class CreateProjectRequest(
    val name: String,
    val description: String? = null
)

@Serializable
data class UpdateProjectRequest(
    val name: String? = null,
    val description: String? = null
)
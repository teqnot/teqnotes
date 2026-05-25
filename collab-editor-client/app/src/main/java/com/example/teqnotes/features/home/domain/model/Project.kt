package com.example.teqnotes.features.home.domain.model

data class Project(
    val id: String,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
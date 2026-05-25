package com.example.teqnotes.features.home.domain.model

data class Note(
    val id: String,
    val title: String,
    val content: String = "",
    val projectId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
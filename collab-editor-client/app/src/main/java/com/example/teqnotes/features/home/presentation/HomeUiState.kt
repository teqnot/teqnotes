package com.example.teqnotes.features.home.presentation

import com.example.teqnotes.features.home.domain.model.Note
import com.example.teqnotes.features.home.domain.model.Project

data class HomeUiState(
    val individualNotes: List<Note> = emptyList(),
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
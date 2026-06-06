package com.example.teqnotes.features.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teqnotes.features.home.domain.model.Note
import com.example.teqnotes.features.home.domain.model.Project
import com.example.teqnotes.features.home.domain.usecase.CreateNoteUseCase
import com.example.teqnotes.features.home.domain.usecase.CreateProjectUseCase
import com.example.teqnotes.features.home.domain.usecase.DeleteNoteUseCase
import com.example.teqnotes.features.home.domain.usecase.GetIndividualNotesUseCase
import com.example.teqnotes.features.home.domain.usecase.GetNotesByProjectUseCase
import com.example.teqnotes.features.home.domain.usecase.GetProjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getIndividualNotesUseCase: GetIndividualNotesUseCase,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val getNotesByProjectUseCase: GetNotesByProjectUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val createProjectUseCase: CreateProjectUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _projectNotes = MutableStateFlow<List<Note>>(emptyList())
    val projectNotes = _projectNotes.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            getIndividualNotesUseCase()
                .catch { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
                .collect { notes ->
                    _uiState.update {
                        it.copy(individualNotes = notes, isLoading = false, error = null)
                    }
                }
        }

        viewModelScope.launch {
            getProjectsUseCase()
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { projects ->
                    _uiState.update { it.copy(projects = projects) }
                }
        }
    }

    fun createNewNote(title: String, description: String, projectId: String? = null) {
        viewModelScope.launch {
            createNoteUseCase(
                Note(
                    id = "0",
                    title = title,
                    content = description,
                    projectId = projectId
                )
            )
            if (projectId != null) {
                loadProjectNotes(projectId)
            } else {
                loadInitialData()
            }
        }
    }

    fun createNewProject(name: String, description: String) {
        viewModelScope.launch {
            createProjectUseCase(
                Project(
                    id = "0",
                    name = name,
                    description = description
                )
            )
            loadInitialData()
        }
    }

    fun loadProjectNotes(projectId: String) {
        viewModelScope.launch {
            getNotesByProjectUseCase(projectId)
                .catch { e -> Log.e("HomeVM", "Failed to load project notes", e) }
                .collect { notes -> _projectNotes.value = notes }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId)
            loadInitialData()
        }
    }
}
package com.example.teqnotes.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teqnotes.features.home.domain.model.Note
import com.example.teqnotes.features.home.domain.model.Project
import com.example.teqnotes.features.home.domain.usecase.CreateNoteUseCase
import com.example.teqnotes.features.home.domain.usecase.CreateProjectUseCase
import com.example.teqnotes.features.home.domain.usecase.GetIndividualNotesUseCase
import com.example.teqnotes.features.home.domain.usecase.GetNotesByProjectUseCase
import com.example.teqnotes.features.home.domain.usecase.GetProjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getIndividualNotesUseCase: GetIndividualNotesUseCase,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val getNotesByProjectUseCase: GetNotesByProjectUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val createProjectUseCase: CreateProjectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _projectNotes = MutableStateFlow<List<Note>>(emptyList())
    val projectNotes = _projectNotes.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            getIndividualNotesUseCase().collect { notes ->
                _uiState.value = _uiState.value.copy(individualNotes = notes)
            }
        }

        viewModelScope.launch {
            getProjectsUseCase().collect { projects ->
                _uiState.value = _uiState.value.copy(projects = projects)
            }
        }

        if (_uiState.value.individualNotes.isEmpty() && _uiState.value.projects.isEmpty()) {
            loadMockData()
        }
    }

    fun createNewNote(title: String, description: String, projectId: String? = null) {
        viewModelScope.launch {
            createNoteUseCase(
                Note(
                    id = "note_${System.currentTimeMillis()}",
                    title = title,
                    content = description,
                    projectId = projectId,
                    timestamp = System.currentTimeMillis()
                )
            )

            projectId?.let {
                loadProjectNotes(it)
            }
        }
    }

    fun createNewProject(name: String, description: String, friendEmails: String? = null) {
        viewModelScope.launch {
            createProjectUseCase(
                Project(
                    id = "project_${System.currentTimeMillis()}",
                    name = name,
                    description = description,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    private fun loadMockData() {
        viewModelScope.launch {
            createNoteUseCase(
                Note(
                    id = "note_1",
                    title = "Курсач",
                    content = "Lorem ipsum dolor sit amet",
                    timestamp = System.currentTimeMillis()
                )
            )

            createNoteUseCase(
                Note(
                    id = "note_2",
                    title = "Пляж",
                    content = "Consectetur adipiscing elit",
                    timestamp = System.currentTimeMillis() - 3600000
                )
            )

            createProjectUseCase(
                Project(
                    id = "project_1",
                    name = "Проект 1",
                    description = "Lorem ipsum",
                    createdAt = System.currentTimeMillis()
                )
            )

            createProjectUseCase(
                Project(
                    id = "project_2",
                    name = "Проект 2",
                    description = "Dolor sit amet",
                    createdAt = System.currentTimeMillis() - 7200000
                )
            )
        }
    }

    fun loadProjectNotes(projectId: String) {
        viewModelScope.launch {
            getNotesByProjectUseCase(projectId).collect { notes ->
                _projectNotes.value = notes
            }
        }
    }

    fun createNewNote(title: String) {
        viewModelScope.launch {
            createNoteUseCase(
                Note(
                    id = "note_${System.currentTimeMillis()}",
                    title = title,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun createNewProject(name: String) {
        viewModelScope.launch {
            createProjectUseCase(
                Project(
                    id = "project_${System.currentTimeMillis()}",
                    name = name,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }
}
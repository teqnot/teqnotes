package com.example.teqnotes.features.note.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teqnotes.features.home.domain.model.Note
import com.example.teqnotes.features.home.domain.usecase.GetNoteByIdUseCase
import com.example.teqnotes.features.home.domain.usecase.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteEditorUiState(
    val note: Note? = null,
    val projectName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState = _uiState.asStateFlow()

    fun loadNote(noteId: String) {
        viewModelScope.launch {
            getNoteByIdUseCase(noteId).collect { note ->
                _uiState.value = _uiState.value.copy(
                    note = note,
                    isLoading = false
                )
            }
        }
    }

    fun updateNoteTitle(title: String) {
        val currentNote = _uiState.value.note ?: return

        viewModelScope.launch {
            val updatedNote = currentNote.copy(title = title)
            updateNoteUseCase(updatedNote)
            _uiState.value = _uiState.value.copy(note = updatedNote)
        }
    }

    fun updateNoteContent(content: String) {
        val currentNote = _uiState.value.note ?: return

        viewModelScope.launch {
            val updatedNote = currentNote.copy(content = content)
            updateNoteUseCase(updatedNote)
            _uiState.value = _uiState.value.copy(note = updatedNote)
        }
    }
}
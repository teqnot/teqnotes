package com.example.teqnotes.features.note.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teqnotes.features.friends.domain.model.Friend
import com.example.teqnotes.features.friends.domain.usecase.GetFriendsUseCase
import com.example.teqnotes.features.home.domain.model.Note
import com.example.teqnotes.features.home.domain.usecase.GetNoteByIdUseCase
import com.example.teqnotes.features.home.domain.usecase.ShareNoteWithFriendsUseCase
import com.example.teqnotes.features.home.domain.usecase.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
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
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val shareNoteWithFriendsUseCase: ShareNoteWithFriendsUseCase,
    private val getFriendsUseCase: GetFriendsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState = _uiState.asStateFlow()

    private val _shareSheetState = MutableStateFlow(ShareSheetUiState())
    val shareSheetState: StateFlow<ShareSheetUiState> = _shareSheetState.asStateFlow()

    fun loadNote(noteId: String) {
        viewModelScope.launch {
            getNoteByIdUseCase(noteId).collect { note ->
                _uiState.value = _uiState.value.copy(note = note, isLoading = false)
            }
        }
    }

    fun updateNoteTitle(title: String) {
        val currentNote = _uiState.value.note ?: return
        viewModelScope.launch {
            updateNoteUseCase(currentNote.copy(title = title))
            _uiState.value = _uiState.value.copy(note = currentNote.copy(title = title))
        }
    }

    fun updateNoteContent(content: String) {
        val currentNote = _uiState.value.note ?: return
        viewModelScope.launch {
            updateNoteUseCase(currentNote.copy(content = content))
            _uiState.value = _uiState.value.copy(note = currentNote.copy(content = content))
        }
    }

    suspend fun saveChanges(newTitle: String, newContent: String) {
        val original = _uiState.value.note ?: return
        val hasChanges = newTitle != original.title || newContent != original.content

        if (hasChanges) {
            updateNoteUseCase(
                Note(
                    id = original.id,
                    title = newTitle,
                    content = newContent,
                    projectId = original.projectId,
                    timestamp = original.timestamp,
                    isArchived = original.isArchived
                )
            )
        }
    }

    fun openShareSheet() {
        viewModelScope.launch {
            _shareSheetState.update { it.copy(isLoading = true) }

            getFriendsUseCase()
                .catch { e -> _shareSheetState.update { it.copy(error = e.message) } }
                .collect { friends ->
                    _shareSheetState.update {
                        it.copy(
                            friends = friends,
                            isLoading = false,
                            isVisible = true
                        )
                    }
                }
        }
    }

    fun closeShareSheet() {
        _shareSheetState.update { it.copy(isVisible = false, selectedFriendIds = emptySet()) }
    }

    fun updateSelectedFriends(ids: Set<String>) {
        _shareSheetState.update { it.copy(selectedFriendIds = ids) }
    }

    fun shareNoteWithSelectedFriends(noteId: String, role: String = "READ_WRITE") {
        val state = _shareSheetState.value
        if (state.selectedFriendIds.isEmpty()) return

        viewModelScope.launch {
            val noteIdInt = noteId.toIntOrNull() ?: run {
                println("[ShareVM] Invalid noteId: $noteId")
                return@launch
            }

            Log.d("DEBUG","[ShareVM] selectedFriendIds (String): ${state.selectedFriendIds}")

            val friendIds = state.selectedFriendIds.mapNotNull { idStr ->
                val intId = idStr.toIntOrNull()
                Log.d("DEBUG", "[ShareVM] Converting friend ID: '$idStr' -> $intId")
                intId
            }

            Log.d("DEBUG","[ShareVM] Final friendIds (Int) to send: $friendIds")

            shareNoteWithFriendsUseCase(noteIdInt, friendIds, role)
                .onSuccess {
                    Log.d("DEBUG","[ShareVM] Share request sent successfully")
                    closeShareSheet()
                }
                .onFailure { error ->
                    Log.d("DEBUG","[ShareVM] Share failed: ${error.message}")
                    _shareSheetState.update { it.copy(error = error.message) }
                }
        }
    }
}

data class ShareSheetUiState(
    val isVisible: Boolean = false,
    val friends: List<Friend> = emptyList(),
    val selectedFriendIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)
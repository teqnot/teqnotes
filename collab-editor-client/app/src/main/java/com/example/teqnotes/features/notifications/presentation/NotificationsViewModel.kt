package com.example.teqnotes.features.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teqnotes.features.friends.domain.repository.FriendRepository
import com.example.teqnotes.features.notifications.domain.model.FriendRequestNotification
import com.example.teqnotes.features.notifications.domain.repository.NotificationRepository
import com.example.teqnotes.features.notifications.domain.usecase.GetNotificationsUseCase
import com.example.teqnotes.features.notifications.domain.usecase.MarkAsReadUseCase
import com.example.teqnotes.features.notifications.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationRepository,
    private val friendRepository: FriendRepository,
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val markAsReadUseCase: MarkAsReadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private val _friendRequests = MutableStateFlow<List<FriendRequestNotification>>(emptyList())
    val friendRequests: StateFlow<List<FriendRequestNotification>> = _friendRequests.asStateFlow()

    init {
        loadNotifications()
        loadFriendRequests()
    }

    fun refresh() {
        loadNotifications()
        loadFriendRequests()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            getNotificationsUseCase().collect { notifications ->
                _uiState.update { it.copy(notifications = notifications) }
            }
        }
    }

    private fun loadFriendRequests() {
        viewModelScope.launch {
            friendRepository.getIncomingRequests()
                .onSuccess { notifications ->
                    _friendRequests.value = notifications
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message) }
                }
        }
    }

    fun acceptFriendRequest(requestId: Int) {
        viewModelScope.launch {
            friendRepository.acceptFriendRequest(requestId)
                .onSuccess {
                    _friendRequests.update { list -> list.filter { it.id != requestId } }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message) }
                }
        }
    }

    fun rejectFriendRequest(requestId: Int) {
        viewModelScope.launch {
            friendRepository.rejectFriendRequest(requestId)
                .onSuccess {
                    _friendRequests.update { list -> list.filter { it.id != requestId } }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message) }
                }
        }
    }

    fun toggleBookmarksFilter() {
        _uiState.update { it.copy(showBookmarksOnly = !it.showBookmarksOnly) }
    }

    fun toggleBookmark(id: String) {
        viewModelScope.launch { toggleBookmarkUseCase(id) }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch { markAsReadUseCase(id) }
    }
}
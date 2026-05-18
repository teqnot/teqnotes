package com.example.teqnotes.features.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teqnotes.features.notifications.data.local.NotificationEntity
import com.example.teqnotes.features.notifications.domain.repository.NotificationRepository
import com.example.teqnotes.features.notifications.domain.usecase.GetNotificationsUseCase
import com.example.teqnotes.features.notifications.domain.usecase.MarkAsReadUseCase
import com.example.teqnotes.features.notifications.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(

    private val repository: NotificationRepository,

    private val getNotificationsUseCase:
    GetNotificationsUseCase,

    private val toggleBookmarkUseCase:
    ToggleBookmarkUseCase,

    private val markAsReadUseCase:
    MarkAsReadUseCase

) : ViewModel() {

    private val _uiState =
        MutableStateFlow(NotificationsUiState())

    val uiState =
        _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val mockEntities = listOf(
                NotificationEntity(
                    id = "1",
                    userName = "user",
                    noteName = "Курсовая",
                    timestamp = System.currentTimeMillis() - 17 * 60 * 1000,
                    changeTitle = "Upgrade to-do block",
                    changeSubtitle = "Task \"Титульник\" changed to...",
                    isRead = false,
                    isBookmarked = false
                ),
                NotificationEntity(
                    id = "2",
                    userName = "friend_no1",
                    noteName = "Поездка",
                    timestamp = System.currentTimeMillis() - 56 * 60 * 1000,
                    changeTitle = "Delete \"Budget\" block",
                    changeSubtitle = "Deleted block \"Budget\" to st...",
                    isRead = false,
                    isBookmarked = false
                ),
                NotificationEntity(
                    id = "3",
                    userName = "friend_no1",
                    noteName = "Поездка",
                    timestamp = System.currentTimeMillis() - 3 * 60 * 60 * 1000,
                    changeTitle = "create \"Budget\" block",
                    changeSubtitle = "added \"Budget\" block to proj...",
                    isRead = false,
                    isBookmarked = false
                )
            )

            repository.insertNotifications(mockEntities)
        }

        viewModelScope.launch {
            getNotificationsUseCase()
                .collect { notifications ->
                    _uiState.update {
                        it.copy(notifications = notifications)
                    }
                }
        }
    }

    fun toggleBookmarksFilter() {

        _uiState.update {

            it.copy(
                showBookmarksOnly =
                    !it.showBookmarksOnly
            )
        }
    }

    fun toggleBookmark(id: String) {

        viewModelScope.launch {
            toggleBookmarkUseCase(id)
        }
    }

    fun markAsRead(id: String) {

        viewModelScope.launch {
            markAsReadUseCase(id)
        }
    }
}
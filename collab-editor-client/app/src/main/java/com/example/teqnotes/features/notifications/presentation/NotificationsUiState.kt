package com.example.teqnotes.features.notifications.presentation

import com.example.teqnotes.features.notifications.domain.model.Notification

data class NotificationsUiState(

    val notifications: List<Notification> = emptyList(),

    val showBookmarksOnly: Boolean = false,
    val error: String? = null
)
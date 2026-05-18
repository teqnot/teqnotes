package com.example.teqnotes.features.notifications.domain.repository

import com.example.teqnotes.features.notifications.data.local.NotificationEntity
import com.example.teqnotes.features.notifications.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun getNotifications():
            Flow<List<Notification>>

    suspend fun markAsRead(id: String)

    suspend fun toggleBookmark(id: String)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)
}
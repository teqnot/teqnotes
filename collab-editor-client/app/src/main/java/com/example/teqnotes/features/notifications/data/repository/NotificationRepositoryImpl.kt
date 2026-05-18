package com.example.teqnotes.features.notifications.data.repository

import com.example.teqnotes.features.notifications.data.local.NotificationDao
import com.example.teqnotes.features.notifications.data.local.NotificationEntity
import com.example.teqnotes.features.notifications.domain.model.Notification
import com.example.teqnotes.features.notifications.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val dao: NotificationDao
) : NotificationRepository {

    override fun getNotifications():
            Flow<List<Notification>> {

        return dao.getNotifications().map { entities ->

            entities.map { entity ->

                Notification(
                    id = entity.id,
                    userName = entity.userName,
                    noteName = entity.noteName,
                    timestamp = entity.timestamp,
                    changeTitle = entity.changeTitle,
                    changeSubtitle = entity.changeSubtitle,
                    isRead = entity.isRead,
                    isBookmarked = entity.isBookmarked
                )
            }
        }
    }

    override suspend fun markAsRead(id: String) {
        dao.markAsRead(id)
    }

    override suspend fun toggleBookmark(id: String) {
        dao.toggleBookmark(id)
    }

    override suspend fun insertNotifications(notifications: List<NotificationEntity>) {
        dao.insertNotifications(notifications)
    }
}
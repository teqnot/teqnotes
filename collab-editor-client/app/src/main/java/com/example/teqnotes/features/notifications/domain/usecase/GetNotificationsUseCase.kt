package com.example.teqnotes.features.notifications.domain.usecase

import com.example.teqnotes.features.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {

    operator fun invoke() =
        repository.getNotifications()
}
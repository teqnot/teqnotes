package com.example.teqnotes.features.notifications.domain.usecase

import com.example.teqnotes.features.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: NotificationRepository
) {

    suspend operator fun invoke(id: String) {
        repository.toggleBookmark(id)
    }
}
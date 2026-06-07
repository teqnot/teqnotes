package com.example.teqnotes.features.notifications.domain.model

data class FriendRequestNotification(
    val id: Int,
    val senderId: Int,
    val senderName: String,
    val senderEmail: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
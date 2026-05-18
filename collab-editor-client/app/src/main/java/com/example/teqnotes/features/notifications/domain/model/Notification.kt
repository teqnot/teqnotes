package com.example.teqnotes.features.notifications.domain.model

data class Notification(

    val id: String,

    val userName: String,
    val noteName: String,

    val timestamp: Long,

    val changeTitle: String,
    val changeSubtitle: String,

    val isRead: Boolean,
    val isBookmarked: Boolean
)
package com.example.teqnotes.features.notifications.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(

    @PrimaryKey
    val id: String,

    val userName: String,
    val noteName: String,

    val timestamp: Long,

    val changeTitle: String,
    val changeSubtitle: String,

    val isRead: Boolean = false,
    val isBookmarked: Boolean = false
)
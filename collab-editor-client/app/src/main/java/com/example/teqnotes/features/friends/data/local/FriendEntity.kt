package com.example.teqnotes.features.friends.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isBlocked: Boolean = false
)
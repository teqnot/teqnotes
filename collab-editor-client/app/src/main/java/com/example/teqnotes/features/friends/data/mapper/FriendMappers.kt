package com.example.teqnotes.features.friends.data.mapper

import com.example.teqnotes.features.friends.data.local.FriendEntity
import com.example.teqnotes.features.friends.data.remote.FriendDto
import com.example.teqnotes.features.friends.data.remote.FriendshipResponseDto
import com.example.teqnotes.features.friends.data.remote.UserSearchResultDto
import com.example.teqnotes.features.friends.domain.model.Friend
import com.example.teqnotes.features.notifications.domain.model.FriendRequestNotification

fun FriendDto.toEntity(): FriendEntity = FriendEntity(
    id = id.toString(),
    name = name,
    email = email,
    avatarUrl = "",
    createdAt = System.currentTimeMillis(),
    isBlocked = false,
    isPending = status == "pending"
)

fun FriendEntity.toDomain(): Friend = Friend(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl,
    createdAt = createdAt,
    isPending = isPending
)

fun Friend.toEntity(): FriendEntity = FriendEntity(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl,
    createdAt = createdAt,
    isBlocked = false,
    isPending = isPending
)

fun UserSearchResultDto.toDomainFriend(): Friend = Friend(
    id = id.toString(),
    name = name,
    email = email,
    avatarUrl = "",
    createdAt = System.currentTimeMillis(),
    isPending = false
)

fun FriendshipResponseDto.toNotification(): FriendRequestNotification =
    FriendRequestNotification(
        id = id,
        senderId = userId,
        senderName = name,
        senderEmail = email,
        timestamp = System.currentTimeMillis()
    )
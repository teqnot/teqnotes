package com.example.teqnotes.features.friends.domain.repository

import com.example.teqnotes.features.friends.domain.model.Friend
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    fun getFriends(): Flow<List<Friend>>
    suspend fun addFriend(friend: Friend)
    suspend fun blockFriend(id: String)
}
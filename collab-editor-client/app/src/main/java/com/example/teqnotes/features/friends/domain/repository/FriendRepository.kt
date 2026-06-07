package com.example.teqnotes.features.friends.domain.repository

import com.example.teqnotes.features.friends.domain.model.Friend
import com.example.teqnotes.features.notifications.domain.model.FriendRequestNotification
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    fun getFriends(): Flow<List<Friend>>
    fun searchUsers(query: String): Flow<List<Friend>>
    suspend fun addFriend(friend: Friend): Result<Unit>
    suspend fun removeFriend(id: String): Result<Unit>
    suspend fun blockFriend(id: String): Result<Unit>
    suspend fun sendFriendRequestByEmail(email: String): Result<Unit>
    suspend fun acceptFriendRequest(requestId: Int): Result<Unit>
    suspend fun rejectFriendRequest(requestId: Int): Result<Unit>
    suspend fun getIncomingRequests(): Result<List<FriendRequestNotification>>
}
package com.example.teqnotes.features.friends.data.repository

import android.util.Log
import com.example.teqnotes.features.friends.data.local.FriendDao
import com.example.teqnotes.features.friends.data.mapper.toDomain
import com.example.teqnotes.features.friends.data.mapper.toDomainFriend
import com.example.teqnotes.features.friends.data.mapper.toEntity
import com.example.teqnotes.features.friends.data.mapper.toNotification
import com.example.teqnotes.features.friends.data.remote.FriendApi
import com.example.teqnotes.features.friends.data.remote.FriendRequestDto
import com.example.teqnotes.features.friends.domain.model.Friend
import com.example.teqnotes.features.friends.domain.repository.FriendRepository
import com.example.teqnotes.features.notifications.domain.model.FriendRequestNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.emptyList

class FriendRepositoryImpl @Inject constructor(
    private val friendApi: FriendApi,
    private val friendDao: FriendDao
) : FriendRepository {

    override fun getFriends(): Flow<List<Friend>> = flow {
        friendApi.getFriends()
            .onSuccess { dtos ->
                val entities = dtos.map { it.toEntity() }
                friendDao.insertFriends(entities)
            }
            .onFailure { Log.e("FriendRepo", "Failed to fetch friends", it) }

        friendDao.getFriends().collect { entities ->
            emit(entities.map { it.toDomain() })
        }
    }.flowOn(Dispatchers.IO)

    override fun searchUsers(query: String): Flow<List<Friend>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }

        val apiResult = friendApi.searchUsers(query)

        if (apiResult.isSuccess) {
            val dtos = apiResult.getOrNull() ?: emptyList()

            val existingFriendIds = withContext(Dispatchers.IO) {
                friendDao.getFriends().first().map { it.id }
            }

            val results = dtos
                .filter { it.id.toString() !in existingFriendIds }
                .map { it.toDomainFriend() }

            emit(results)
        } else {
            Log.e("FriendRepo", "Failed to search users", apiResult.exceptionOrNull())
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addFriend(friend: Friend): Result<Unit> = runCatching {
        friendDao.insertFriend(friend.toEntity())
    }

    override suspend fun removeFriend(id: String): Result<Unit> = runCatching {
        friendDao.deleteFriend(id)
    }

    override suspend fun blockFriend(id: String): Result<Unit> = runCatching {
        friendDao.blockFriend(id)
    }

    override suspend fun sendFriendRequestByEmail(email: String): Result<Unit> {
        return friendApi.sendRequest(FriendRequestDto(friendEmail = email))
            .onFailure { Log.e("FriendRepo", "Failed to send request to $email", it) }
    }

    override suspend fun acceptFriendRequest(requestId: Int): Result<Unit> {
        return friendApi.acceptRequest(requestId)
            .onFailure { Log.e("FriendRepo", "Failed to accept request #$requestId", it) }
    }

    override suspend fun rejectFriendRequest(requestId: Int): Result<Unit> {
        return friendApi.rejectRequest(requestId)
            .onFailure { Log.e("FriendRepo", "Failed to reject request #$requestId", it) }
    }

    override suspend fun getIncomingRequests(): Result<List<FriendRequestNotification>> {
        return friendApi.getIncomingRequests()
            .map { dtos -> dtos.map { it.toNotification() } }
            .onFailure { Log.e("FriendRepo", "Failed to fetch incoming requests", it) }
    }
}
package com.example.teqnotes.features.friends.data.repository

import com.example.teqnotes.features.friends.data.FriendEntity
import com.example.teqnotes.features.friends.data.local.FriendDao
import com.example.teqnotes.features.friends.domain.model.Friend
import com.example.teqnotes.features.friends.domain.repository.FriendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val friendDao: FriendDao
) : FriendRepository {

    override fun getFriends(): Flow<List<Friend>> {
        return friendDao.getFriends().map { entities ->
            entities.map { entity ->
                Friend(
                    id = entity.id,
                    name = entity.name,
                    email = entity.email,
                    avatarUrl = entity.avatarUrl,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun addFriend(friend: Friend) {
        friendDao.insertFriend(
            FriendEntity(
                id = friend.id,
                name = friend.name,
                email = friend.email,
                avatarUrl = friend.avatarUrl,
                createdAt = friend.createdAt,
                isBlocked = false
            )
        )
    }

    override suspend fun blockFriend(id: String) {
        friendDao.blockFriend(id)
    }
}
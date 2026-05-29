package com.example.features.friends.data.repository

import com.example.features.friends.data.local.Friendship
import com.example.features.friends.domain.model.FriendshipData
import com.example.features.friends.data.local.Friendships
import com.example.features.auth.data.model.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction

interface FriendshipRepository {
    fun findByUsers(userId1: Int, userId2: Int): FriendshipData?
    fun findById(id: Int): FriendshipData?
    fun create(userId1: Int, userId2: Int): FriendshipData
    fun updateStatus(id: Int, status: String)
    fun getAcceptedFriends(userId: Int): List<FriendshipData>
    fun delete(id: Int)
    fun findPendingRequestsForUser(userId: Int): List<FriendshipData>
}

object FriendshipRepositoryImpl : FriendshipRepository {

    override fun findByUsers(userId1: Int, userId2: Int): FriendshipData? = transaction {
        val friendship = Friendship.find {
            (Friendships.userId1 eq userId1 and (Friendships.userId2 eq userId2)) or
                    (Friendships.userId1 eq userId2 and (Friendships.userId2 eq userId1))
        }.firstOrNull()

        friendship?.let { toFriendshipData(it) }
    }

    override fun findById(id: Int): FriendshipData? = transaction {
        val friendship = Friendship.findById(id) ?: return@transaction null
        toFriendshipData(friendship)
    }

    override fun create(userId1: Int, userId2: Int): FriendshipData = transaction {
        val friendship = Friendship.new {
            this.user1 = User[userId1]
            this.user2 = User[userId2]
            this.status = "pending"
        }
        toFriendshipData(friendship)
    }

    override fun updateStatus(id: Int, status: String) = transaction {
        val friendship = Friendship.findById(id) ?: throw IllegalArgumentException("Friendship not found")
        friendship.status = status
    }

    override fun getAcceptedFriends(userId: Int): List<FriendshipData> = transaction {
        Friendship.find {
            ((Friendships.userId1 eq userId) or (Friendships.userId2 eq userId)) and
                    (Friendships.status eq "accepted")
        }.map { toFriendshipData(it) }
    }

    override fun delete(id: Int): Unit = transaction {
        Friendship.findById(id)?.delete()
    }

    override fun findPendingRequestsForUser(userId: Int): List<FriendshipData> = transaction {
        Friendship.find {
            (Friendships.userId2 eq userId) and (Friendships.status eq "pending")
        }.map { toFriendshipData(it) }
    }

    private fun toFriendshipData(f: Friendship): FriendshipData = FriendshipData(
        id = f.id.value,
        userId1 = f.user1.id.value,
        userId2 = f.user2.id.value,
        status = f.status,
        createdAt = f.createdAt
    )
}
package com.example.repository

import com.example.model.Friendship
import com.example.model.Friendships
import com.example.model.Friendships.userId1
import com.example.model.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction

interface FriendshipRepository {
    fun findByUsers(userId1: Int, userId2: Int): Friendship?
    fun create(userId1: Int, userId2: Int): Friendship
    fun updateStatus(id: Int, status: String)
    fun getAcceptedFriends(userId: Int): List<Friendship>
    fun delete(id: Int)
    fun findPendingRequestsForUser(userId: Int): List<Friendship>
}

object FriendshipRepositoryImpl : FriendshipRepository {
    override fun findByUsers(userId1: Int, userId2: Int): Friendship? = transaction {
        Friendship.find {
            (Friendships.userId1 eq userId1 and (Friendships.userId2 eq userId2)) or
                    (Friendships.userId1 eq userId2 and (Friendships.userId2 eq userId1))
        }.firstOrNull()
    }

    override fun create(userId1: Int, userId2: Int): Friendship = transaction {
        Friendship.new {
            this.user1 = User[userId1]
            this.user2 = User[userId2]
            this.status = "pending"
        }
    }

    override fun updateStatus(id: Int, status: String) = transaction {
        val friendship = Friendship.findById(id) ?: throw IllegalArgumentException("Friendship not found")
        friendship.status = status
    }

    override fun getAcceptedFriends(userId: Int): List<Friendship> = transaction {
        Friendship.find {
            ((Friendships.userId1 eq userId1) or (Friendships.userId2 eq userId1)) and
                    (Friendships.status eq "accepted")
        }.toList()
    }

    override fun delete(id: Int): Unit = transaction {
        Friendship.findById(id)?.delete()
    }

    override fun findPendingRequestsForUser(userId: Int): List<Friendship> = transaction {
        Friendship.find {
            (Friendships.userId2 eq userId) and (Friendships.status eq "pending")
        }.toList()
    }
}
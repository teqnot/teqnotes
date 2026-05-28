package com.example.service

import com.example.dto.FriendshipResponse
import com.example.model.Friendship
import com.example.model.User
import com.example.repository.FriendshipRepository
import com.example.repository.UserRepository

class FriendshipService(
    private val friendshipRepository: FriendshipRepository,
    private val userRepository: UserRepository
) {

    fun sendRequest(currentUserId: Int, friendEmail: String): Result<FriendshipResponse> {
        return try {
            val friend = userRepository.findByEmail(friendEmail)
                ?: throw IllegalArgumentException("User with this email not found")

            if (currentUserId == friend.id.value) {
                throw IllegalArgumentException("You cannot befriend yourself")
            }

            val existing = friendshipRepository.findByUsers(currentUserId, friend.id.value)
            if (existing != null) {
                if (existing.status == "accepted") throw IllegalArgumentException("You are already friends")
                if (existing.status == "pending") throw IllegalArgumentException("Request already pending")
                throw IllegalArgumentException("Friendship status conflict")
            }

            val friendship = friendshipRepository.create(currentUserId, friend.id.value)

            Result.success(FriendshipResponse(
                id = friendship.id.value,
                userId = friend.id.value,
                name = friend.name,
                email = friend.email,
                status = "pending"
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun acceptRequest(currentUserId: Int, requestId: Int): Result<Unit> {
        return try {
            val friendship = Friendship.findById(requestId)
                ?: throw IllegalArgumentException("Request not found")

            if (friendship.user2.id.value != currentUserId) {
                throw IllegalArgumentException("This request is not for you")
            }

            if (friendship.status != "pending") {
                throw IllegalArgumentException("Request is no longer pending")
            }

            friendshipRepository.updateStatus(requestId, "accepted")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun removeFriendOrReject(currentUserId: Int, requestId: Int): Result<Unit> {
        return try {
            val friendship = Friendship.findById(requestId)
                ?: throw IllegalArgumentException("Relationship not found")

            if (friendship.user1.id.value != currentUserId && friendship.user2.id.value != currentUserId) {
                throw IllegalArgumentException("Forbidden")
            }

            friendshipRepository.delete(requestId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getFriends(currentUserId: Int): Result<List<FriendshipResponse>> {
        return try {
            val friendships = friendshipRepository.getAcceptedFriends(currentUserId)
            val friends = friendships.map { f ->
                val otherUserId = if (f.user1.id.value == currentUserId) f.user2.id.value else f.user1.id.value
                val user = User[otherUserId]

                FriendshipResponse(
                    id = f.id.value,
                    userId = user.id.value,
                    name = user.name,
                    email = user.email,
                    status = "accepted"
                )
            }
            Result.success(friends)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getIncomingRequests(currentUserId: Int): Result<List<FriendshipResponse>> {
        return try {
            val requests = friendshipRepository.findPendingRequestsForUser(currentUserId)
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
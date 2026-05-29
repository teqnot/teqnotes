package com.example.features.friends.domain.service

import com.example.features.friends.presentation.dto.FriendshipResponse
import com.example.features.auth.data.repository.UserRepository
import com.example.features.friends.data.repository.FriendshipRepository

class FriendshipService(
private val friendshipRepository: FriendshipRepository,
private val userRepository: UserRepository
) {

    fun sendRequest(currentUserId: Int, friendEmail: String): Result<FriendshipResponse> {
        return try {
            val friend = userRepository.findByEmail(friendEmail)
                ?: throw IllegalArgumentException("User with this email not found")

            if (currentUserId == friend.id) {
                throw IllegalArgumentException("You cannot befriend yourself")
            }

            val existing = friendshipRepository.findByUsers(currentUserId, friend.id)
            if (existing != null) {
                when (existing.status) {
                    "accepted" -> throw IllegalArgumentException("You are already friends")
                    "pending" -> throw IllegalArgumentException("Request already pending")
                    else -> throw IllegalArgumentException("Friendship status conflict")
                }
            }

            val friendship = friendshipRepository.create(currentUserId, friend.id)

            Result.success(
                FriendshipResponse(
                    id = friendship.id,
                    userId = friend.id,
                    name = friend.name,
                    email = friend.email,
                    status = "pending"
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun acceptRequest(currentUserId: Int, requestId: Int): Result<Unit> {
        return try {
            val friendship = friendshipRepository.findById(requestId)
                ?: throw IllegalArgumentException("Request not found")

            if (friendship.userId2 != currentUserId) {
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
            val friendship = friendshipRepository.findById(requestId)
                ?: throw IllegalArgumentException("Relationship not found")

            if (friendship.userId1 != currentUserId && friendship.userId2 != currentUserId) {
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
                val otherUserId = if (f.userId1 == currentUserId) f.userId2 else f.userId1

                val user = userRepository.findById(otherUserId)
                    ?: throw IllegalStateException("User not found")

                FriendshipResponse(
                    id = f.id,
                    userId = user.id,
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
            val responses = requests.map { req ->
                val sender = userRepository.findById(req.userId1)
                    ?: throw IllegalStateException("Sender not found")

                FriendshipResponse(
                    id = req.id,
                    userId = sender.id,
                    name = sender.name,
                    email = sender.email,
                    status = "pending"
                )
            }
            Result.success(responses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
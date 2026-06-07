package com.example.teqnotes.features.friends.domain.usecase

import com.example.teqnotes.features.friends.domain.repository.FriendRepository
import javax.inject.Inject

class RemoveFriendUseCase @Inject constructor(
    private val repository: FriendRepository
) {
    suspend operator fun invoke(friendId: String): Result<Unit> =
        repository.removeFriend(friendId)
}
package com.example.teqnotes.features.friends.domain.usecase

import com.example.teqnotes.features.friends.domain.repository.FriendRepository
import javax.inject.Inject

class SendFriendRequestUseCase @Inject constructor(
    private val repository: FriendRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> =
        repository.sendFriendRequestByEmail(email)
}
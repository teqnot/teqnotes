package com.example.teqnotes.features.friends.domain.usecase

import com.example.teqnotes.features.friends.domain.model.Friend
import com.example.teqnotes.features.friends.domain.repository.FriendRepository
import javax.inject.Inject

class AddFriendUseCase @Inject constructor(
    private val repository: FriendRepository
) {
    suspend operator fun invoke(friend: Friend) {
        repository.addFriend(friend)
    }
}
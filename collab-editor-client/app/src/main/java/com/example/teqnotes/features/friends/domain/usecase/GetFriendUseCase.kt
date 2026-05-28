package com.example.teqnotes.features.friends.domain.usecase

import com.example.teqnotes.features.friends.domain.model.Friend
import com.example.teqnotes.features.friends.domain.repository.FriendRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFriendsUseCase @Inject constructor(
    private val repository: FriendRepository
) {
    operator fun invoke(): Flow<List<Friend>> {
        return repository.getFriends()
    }
}
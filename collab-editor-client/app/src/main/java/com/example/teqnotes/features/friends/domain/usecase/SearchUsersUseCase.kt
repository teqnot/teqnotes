package com.example.teqnotes.features.friends.domain.usecase

import com.example.teqnotes.features.friends.domain.model.Friend
import com.example.teqnotes.features.friends.domain.repository.FriendRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val repository: FriendRepository
) {
    operator fun invoke(query: String): Flow<List<Friend>> =
        repository.searchUsers(query)
}
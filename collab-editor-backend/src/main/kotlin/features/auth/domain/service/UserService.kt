package com.example.features.auth.domain.service

import com.example.features.auth.data.repository.UserRepository
import com.example.shared.dto.UserSearchResult

class UserService(
    private val userRepository: UserRepository
) {
    fun searchUsers(currentUserId: Int, query: String): Result<List<UserSearchResult>> = try {
        val users = userRepository.searchUsers(query)
            .filter { it.id != currentUserId }
            .map { UserSearchResult(id = it.id, name = it.name, email = it.email) }
        Result.success(users)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
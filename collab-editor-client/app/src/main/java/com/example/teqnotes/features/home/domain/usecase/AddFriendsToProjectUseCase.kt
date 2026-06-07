package com.example.teqnotes.features.home.domain.usecase

import com.example.teqnotes.features.home.domain.repository.HomeRepository
import javax.inject.Inject

class AddFriendsToProjectUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(projectId: Int, friendEmails: List<String>, role: String): Result<Unit> =
        repository.addFriendsToProject(projectId, friendEmails, role)
}
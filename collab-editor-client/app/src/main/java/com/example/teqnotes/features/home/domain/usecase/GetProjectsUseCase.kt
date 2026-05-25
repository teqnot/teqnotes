package com.example.teqnotes.features.home.domain.usecase

import com.example.teqnotes.features.home.domain.model.Project
import com.example.teqnotes.features.home.domain.repository.HomeRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetProjectsUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(): Flow<List<Project>> {
        return repository.getProjects()
    }
}
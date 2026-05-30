package com.example.teqnotes.features.home.domain.usecase

import com.example.teqnotes.features.home.domain.model.Project
import com.example.teqnotes.features.home.domain.repository.HomeRepository
import javax.inject.Inject

class CreateProjectUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(project: Project) {
        repository.createProject(project)
    }
}
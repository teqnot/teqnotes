package com.example.teqnotes.features.auth.domain.usecase

import com.example.teqnotes.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
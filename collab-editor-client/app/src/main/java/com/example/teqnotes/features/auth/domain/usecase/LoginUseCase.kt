package com.example.teqnotes.features.auth.domain.usecase

import com.example.teqnotes.features.auth.domain.model.User
import com.example.teqnotes.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank() || password.length < 6) {
            return Result.failure(IllegalArgumentException("Invalid credentials"))
        }
        return repository.login(email, password)
    }
}
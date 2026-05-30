package com.example.teqnotes.features.auth.domain.usecase

import com.example.teqnotes.features.auth.domain.model.User
import com.example.teqnotes.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String): Result<User> {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(IllegalArgumentException("Invalid email"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password too short"))
        }
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Name is required"))
        }
        return repository.register(email, password, name)
    }
}
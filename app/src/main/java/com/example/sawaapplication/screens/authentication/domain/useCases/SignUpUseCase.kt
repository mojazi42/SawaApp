package com.example.sawaapplication.screens.authentication.domain.useCases

import com.example.sawaapplication.screens.authentication.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        if (password != confirmPassword) {
            return Result.failure(IllegalArgumentException("Passwords do not match"))
        }
        return repository.signUp(name, email, password)
    }
}
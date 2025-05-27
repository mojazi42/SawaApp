package com.example.sawaapplication.screens.authentication.domain.useCases

import com.example.sawaapplication.screens.authentication.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return try {
            authRepository.sendPasswordResetEmail(email)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.example.sawaapplication.screens.profile.domain.useCases

import com.example.sawaapplication.screens.profile.domain.model.User
import com.example.sawaapplication.screens.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class FetchUserByIdUseCase @Inject constructor(
    private val repository: ProfileRepository
)  {
    suspend operator fun invoke(userId : String): User? {
        return repository.fetchUserById(userId)
    }
}
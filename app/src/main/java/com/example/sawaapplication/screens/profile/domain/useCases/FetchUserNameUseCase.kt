package com.example.sawaapplication.screens.profile.domain.useCases

import com.example.sawaapplication.screens.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class FetchUserNameUseCase @Inject constructor(
    private val repository: ProfileRepository
)  {
    suspend operator fun invoke(userId : String): String? {
        return repository.fetchUserName(userId)
    }
}
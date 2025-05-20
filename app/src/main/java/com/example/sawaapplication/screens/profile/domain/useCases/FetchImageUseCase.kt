package com.example.sawaapplication.screens.profile.domain.useCases

import com.example.sawaapplication.screens.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class FetchImageUseCase @Inject constructor(
    private val repository: ProfileRepository
)  {
    suspend operator fun invoke(userId : String): String? {
        return repository.fetchProfileImageUrl(userId)
    }
}
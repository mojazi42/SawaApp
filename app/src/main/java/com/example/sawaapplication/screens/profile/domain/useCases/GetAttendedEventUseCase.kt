package com.example.sawaapplication.screens.profile.domain.useCases

import com.example.sawaapplication.screens.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetAttendedEventUseCase @Inject constructor(
    private val repository : ProfileRepository
) {
    suspend operator fun invoke(userId: String): List<String>{
        return repository.getAttendedEvent(userId)
    }
}
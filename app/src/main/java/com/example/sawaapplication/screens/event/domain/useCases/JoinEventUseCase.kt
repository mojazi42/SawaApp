package com.example.sawaapplication.screens.event.domain.useCases

import com.example.sawaapplication.screens.event.domain.repository.EventRepository
import javax.inject.Inject

class JoinEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(
        communityId: String,
        eventId: String,
        userId: String
    ): Result<Unit> {
        return repository.joinEvent(communityId, eventId, userId)
    }
}
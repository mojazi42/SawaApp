package com.example.sawaapplication.screens.event.domain.useCases

import com.example.sawaapplication.screens.event.domain.repository.EventRepository
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(communityId: String, eventId: String): Result<Unit> {
        return repository.deleteEvent(communityId, eventId)
    }
}

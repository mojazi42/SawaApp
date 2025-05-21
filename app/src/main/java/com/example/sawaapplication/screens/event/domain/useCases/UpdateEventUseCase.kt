package com.example.sawaapplication.screens.event.domain.useCases

import com.example.sawaapplication.screens.event.domain.repository.EventRepository
import javax.inject.Inject

class UpdateEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(communityId: String, eventId: String, updatedData: Map<String, Any>): Result<Unit> {
        return repository.updateEvent(communityId, eventId, updatedData)
    }
}

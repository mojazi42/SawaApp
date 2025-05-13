package com.example.sawaapplication.screens.event.domain.useCases

import com.example.sawaapplication.screens.event.domain.model.Event
import com.example.sawaapplication.screens.event.domain.repository.EventRepository
import javax.inject.Inject

class GetAllEventInCommunity @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(communityId: String): Result<List<Event>>{
        return repository.fetchEventsFromCommunity(communityId)
    }
}

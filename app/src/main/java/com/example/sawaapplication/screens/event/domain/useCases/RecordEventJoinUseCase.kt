package com.example.sawaapplication.screens.event.domain.useCases

import com.example.sawaapplication.screens.event.domain.repository.EventRepository
import java.util.Date
import javax.inject.Inject

class RecordEventJoinUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(userId: String, eventId: String, eventTitle: String, startTime: Date) {
        repository.recordEventJoin(userId, eventId, eventTitle, startTime)
    }
}

package com.example.sawaapplication.screens.event.domain.useCases

import android.net.Uri
import com.example.sawaapplication.screens.event.domain.model.Event
import com.example.sawaapplication.screens.event.domain.repository.EventRepository
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(communityId: String,event: Event,imageUri: Uri) {
        repository.createEventInCommunity(communityId,event,imageUri)
    }
}

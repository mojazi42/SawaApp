package com.example.sawaapplication.screens.event.domain.repository

import android.net.Uri
import com.example.sawaapplication.screens.event.domain.model.Event

interface EventRepository {
    suspend fun createEventInCommunity(communityId: String, event: Event, imageUri: Uri)
    suspend fun fetchEventsFromCommunity(communityId: String): Result<List<Event>>
    suspend fun joinEvent(communityId: String, eventId: String, userId: String): Result<Unit>
    suspend fun leaveEvent(communityId: String, eventId: String, userId: String): Result<Unit>

}

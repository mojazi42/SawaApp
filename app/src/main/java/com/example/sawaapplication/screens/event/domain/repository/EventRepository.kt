package com.example.sawaapplication.screens.event.domain.repository

import android.net.Uri
import com.example.sawaapplication.screens.event.domain.model.Event
import java.util.Date

interface EventRepository {
    suspend fun createEventInCommunity(communityId: String, event: Event, imageUri: Uri)
    suspend fun fetchEventsFromCommunity(communityId: String): Result<List<Event>>
    suspend fun joinEvent(communityId: String, eventId: String, userId: String): Result<Unit>
    suspend fun leaveEvent(communityId: String, eventId: String, userId: String): Result<Unit>
    suspend fun deleteEvent(communityId: String, eventId: String): Result<Unit>
    suspend fun updateEvent(communityId: String, eventId: String, updatedData: Map<String, Any>): Result<Unit>
    suspend fun getEventById(communityId: String, eventId: String): Event
    suspend fun recordEventJoin(userId: String, eventId: String, eventTitle: String, startTime: Date)
}

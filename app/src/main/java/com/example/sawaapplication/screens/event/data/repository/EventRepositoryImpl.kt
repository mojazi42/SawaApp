package com.example.sawaapplication.screens.event.data.repository

import android.net.Uri
import com.example.sawaapplication.screens.event.data.dataSources.EventInCommunityRemote
import com.example.sawaapplication.screens.event.domain.model.Event
import com.example.sawaapplication.screens.event.domain.repository.EventRepository
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val remoteDataSource: EventInCommunityRemote,

) : EventRepository {

    override suspend fun createEventInCommunity(communityId: String, event: Event, imageUri: Uri) {
        return remoteDataSource.createEventInCommunity(communityId, event, imageUri)
    }

    override suspend fun fetchEventsFromCommunity(communityId: String): Result<List<Event>> {
        return remoteDataSource.fetchEventsFromCommunity(communityId)
    }

    override suspend fun joinEvent(
        communityId: String,
        eventId: String,
        userId: String
    ): Result<Unit> {
        return remoteDataSource.joinEvent(communityId, eventId, userId)
    }

    override suspend fun leaveEvent(
        communityId: String,
        eventId: String,
        userId: String
    ): Result<Unit> {
        return remoteDataSource.leaveEvent(communityId, eventId, userId)
    }

    override suspend fun deleteEvent(communityId: String, eventId: String): Result<Unit> {
        return remoteDataSource.deleteEvent(communityId, eventId)
    }

    override suspend fun updateEvent(
        communityId: String,
        eventId: String,
        updatedData: Map<String, Any>
    ): Result<Unit> {
        return remoteDataSource.updateEvent(communityId, eventId, updatedData)
    }
    override suspend fun getEventById(communityId: String, eventId: String): Event {
        return remoteDataSource.getEventById(communityId, eventId)
    }



}

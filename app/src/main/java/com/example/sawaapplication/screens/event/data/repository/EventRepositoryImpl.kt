package com.example.sawaapplication.screens.event.data.repository

import android.net.Uri
import com.example.sawaapplication.screens.event.data.dataSources.EventInCommunityRemote
import com.example.sawaapplication.screens.event.domain.model.Event
import com.example.sawaapplication.screens.event.domain.repository.EventRepository
import javax.inject.Inject

class EventRepositoryImpl  @Inject constructor(
    private val remoteDataSource: EventInCommunityRemote
) : EventRepository {
    override suspend fun createEventInCommunity(communityId: String,event: Event,imageUri: Uri) {
        return remoteDataSource.createEventInCommunity(communityId,event,imageUri)
    }
}

package com.example.sawaapplication.screens.event.domain.repository

import android.net.Uri
import com.example.sawaapplication.screens.event.domain.model.Event

interface EventRepository {
    suspend fun createEventInCommunity(communityId: String,event: Event,imageUri: Uri)
}

package com.example.sawaapplication.screens.home.domain.useCases

import com.example.sawaapplication.screens.event.domain.model.Event
import com.example.sawaapplication.screens.home.domain.repository.HomeRepository
import com.example.sawaapplication.screens.post.domain.model.Post
import javax.inject.Inject


class FetchJoinedEventsUseCase @Inject constructor(
    private val repository: HomeRepository
)  {
    suspend operator fun invoke(userId:String):  List<Event> {
        return repository.fetchJoinedEvents(userId)
    }
}
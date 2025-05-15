package com.example.sawaapplication.screens.chat.domain.useCases

import com.example.sawaapplication.screens.chat.domain.model.Message
import com.example.sawaapplication.screens.chat.domain.repository.MessageRepository
import com.example.sawaapplication.screens.communities.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMessagesUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    operator fun invoke(communityId: String, currentUserId: String): Flow<List<Message>> {
        return repository.observeMessages(communityId, currentUserId)
    }
}
package com.example.sawaapplication.screens.chat.domain.useCases

import com.example.sawaapplication.screens.chat.domain.repository.MessageRepository
import javax.inject.Inject

class FetchUnreadMessagesUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    operator fun invoke(communityId: String, userId: String, onResult: (Int) -> Unit) {
        repository.fetchUnreadMessages(communityId, userId, onResult)
    }
}

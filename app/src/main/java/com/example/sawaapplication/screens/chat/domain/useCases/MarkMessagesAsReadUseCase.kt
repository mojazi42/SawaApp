package com.example.sawaapplication.screens.chat.domain.useCases

import com.example.sawaapplication.screens.chat.domain.repository.MessageRepository
import javax.inject.Inject

class MarkMessagesAsReadUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(communityId: String, userId: String) {
        repository.markMessagesAsRead(communityId, userId)
    }
}
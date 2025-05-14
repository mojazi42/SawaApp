package com.example.sawaapplication.screens.chat.domain.useCases

import com.example.sawaapplication.screens.chat.domain.model.ChatUserInfo
import com.example.sawaapplication.screens.chat.domain.repository.MessageRepository
import javax.inject.Inject

class GetSenderInfoUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(userId: String): ChatUserInfo? {
        return repository.getSenderInfo(userId)
    }
}

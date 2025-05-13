
package com.example.sawaapplication.screens.chat.domain.useCases

import com.example.sawaapplication.screens.chat.domain.model.Message
import com.example.sawaapplication.screens.chat.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(communityId: String, message: Message) {
        repository.sendMessage(communityId, message)
    }
}

package com.example.sawaapplication.screens.chat.domain.useCases

import com.example.sawaapplication.screens.chat.domain.model.ChatUserInfo
import com.example.sawaapplication.screens.chat.domain.repository.MessageRepository
import javax.inject.Inject

class GetLastMessageWithSenderUseCase @Inject constructor(
    private val repository: MessageRepository
)  {
     suspend operator fun invoke(communityId: String): Pair<String, ChatUserInfo>? {
        return repository.getLastMessageAndSender(communityId)
    }
}

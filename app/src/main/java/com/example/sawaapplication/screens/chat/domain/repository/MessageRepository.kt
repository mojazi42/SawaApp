package com.example.sawaapplication.screens.chat.domain.repository
import com.example.sawaapplication.screens.chat.domain.model.ChatUserInfo
import com.example.sawaapplication.screens.chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendMessage(communityId: String, message: Message)
    fun observeMessages(communityId: String): Flow<List<Message>>
    suspend fun getLastMessageAndSender(communityId: String): Pair<String, ChatUserInfo>?
}

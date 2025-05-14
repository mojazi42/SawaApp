package com.example.sawaapplication.screens.chat.domain.repository
import com.example.sawaapplication.screens.chat.domain.model.ChatUserInfo
import com.example.sawaapplication.screens.chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendMessage(communityId: String, message: Message)
    fun observeMessages(communityId: String, currentUserId: String): Flow<List<Message>>
    suspend fun getLastMessageAndSender(communityId: String): Pair<String, ChatUserInfo>?
    fun fetchUnreadMessages(communityId: String, userId: String, onResult: (Int) -> Unit)
    suspend fun markMessagesAsRead(communityId: String, userId: String)
    suspend fun getSenderInfo(userId: String): ChatUserInfo?
}

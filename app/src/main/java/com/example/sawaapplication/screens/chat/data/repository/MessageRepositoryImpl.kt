package com.example.sawaapplication.screens.chat.data.repository

import com.example.sawaapplication.screens.chat.data.dataSources.remote.MassageRemoteDataSource
import com.example.sawaapplication.screens.chat.domain.model.ChatUserInfo
import com.example.sawaapplication.screens.chat.domain.model.Message
import com.example.sawaapplication.screens.chat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val remoteDataSource: MassageRemoteDataSource
) : MessageRepository {
    override suspend fun sendMessage(communityId: String, message: Message) {
        remoteDataSource.sendMessage(communityId, message.text,  message.senderId)
    }

    override fun observeMessages(communityId: String): Flow<List<Message>> {
        return remoteDataSource.observeMessages(communityId)
    }

    override suspend fun getLastMessageAndSender(communityId: String): Pair<String, ChatUserInfo>? {
        return remoteDataSource.getLastMessageAndSender(communityId)
    }
}


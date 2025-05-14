package com.example.sawaapplication.screens.chat.presentation.vmModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawaapplication.screens.authentication.domain.repository.AuthRepository
import com.example.sawaapplication.screens.chat.domain.model.ChatUserInfo
import com.example.sawaapplication.screens.chat.domain.model.Message
import com.example.sawaapplication.screens.chat.domain.useCases.FetchUnreadMessagesUseCase
import com.example.sawaapplication.screens.chat.domain.useCases.GetLastMessageWithSenderUseCase
import com.example.sawaapplication.screens.chat.domain.useCases.GetSenderInfoUseCase
import com.example.sawaapplication.screens.chat.domain.useCases.MarkMessagesAsReadUseCase
import com.example.sawaapplication.screens.chat.domain.useCases.ObserveMessagesUseCase
import com.example.sawaapplication.screens.chat.domain.useCases.SendMessageUseCase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val getLastMessageWithSenderUseCase: GetLastMessageWithSenderUseCase,
    private val authRepository: AuthRepository,
    private val fetchUnreadMessagesUseCase: FetchUnreadMessagesUseCase,
    private val markMessagesAsReadUseCase: MarkMessagesAsReadUseCase,
    private val getSenderInfoUseCase: GetSenderInfoUseCase
) : ViewModel() {


    val currentUserId = authRepository.getCurrentUserId()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _senderInfo = MutableStateFlow<Map<String, ChatUserInfo>>(emptyMap())
    val senderInfo: StateFlow<Map<String, ChatUserInfo>> get() = _senderInfo

    var currentCommunityIdInView by mutableStateOf<String?>(null)

/*
    fun fetchSenderInfo(userId: String) {
        if (_senderInfo.value.containsKey(userId)) return

        FirebaseFirestore.getInstance()
            .collection("User")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val name = document.getString("name")
                val image = document.getString("image")
                val userInfo = ChatUserInfo(name, image)
                _senderInfo.value = _senderInfo.value + (userId to userInfo)
            }
    }*/
    fun observeMessages(communityId: String) {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                observeMessagesUseCase(communityId, userId).collect { newMessages ->
                    _messages.value = newMessages
                    markMessagesAsRead(communityId, userId)
                }
            }
        }
    }

    fun sendMessage(
        communityId: String,
        messageText: String,
        senderId: String,
    ) {
        val message = Message(
            senderId = senderId,
            text = messageText,
        )

        viewModelScope.launch {
            sendMessageUseCase(communityId, message)
        }
    }

    private val _lastMessageMap =
        MutableStateFlow<Map<String, Pair<String, ChatUserInfo>>>(emptyMap())
    val lastMessageMap: StateFlow<Map<String, Pair<String, ChatUserInfo>>> = _lastMessageMap

    fun fetchLastMessageForCommunity(communityId: String) {
        viewModelScope.launch {
            Log.d("ChatViewModel", "Fetching last message for community: $communityId")
            val result = getLastMessageWithSenderUseCase(communityId)
            if (result != null) {
                Log.d(
                    "ChatViewModel",
                    "Fetched message: ${result.first}, sender: ${result.second.name}"
                )
                _lastMessageMap.value = _lastMessageMap.value + (communityId to result)
            } else {
                Log.w("ChatViewModel", "No last message found for $communityId")
            }
        }
    }

    private val _unreadCount = MutableStateFlow<Map<String, Int>>(emptyMap())
    val unreadCount: StateFlow<Map<String, Int>> = _unreadCount

    fun fetchUnreadMessages(communityId: String, userId: String) {
        fetchUnreadMessagesUseCase(communityId, userId) { count ->
            _unreadCount.value = _unreadCount.value + (communityId to count)
        }
    }

    fun markMessagesAsRead(communityId: String, userId: String) {
        viewModelScope.launch {
            markMessagesAsReadUseCase(communityId, userId)
        }
    }

    fun fetchSenderInfo(userId: String) {
        viewModelScope.launch {
            if (!_senderInfo.value.containsKey(userId)) {
                val info = getSenderInfoUseCase(userId)
                info?.let {
                    _senderInfo.value = _senderInfo.value + (userId to it)
                }
            }
        }
    }
}

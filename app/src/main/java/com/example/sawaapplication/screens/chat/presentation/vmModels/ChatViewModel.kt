package com.example.sawaapplication.screens.chat.presentation.vmModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawaapplication.screens.authentication.domain.repository.AuthRepository
import com.example.sawaapplication.screens.chat.domain.model.ChatUserInfo
import com.example.sawaapplication.screens.chat.domain.model.Message
import com.example.sawaapplication.screens.chat.domain.useCases.GetLastMessageWithSenderUseCase
import com.example.sawaapplication.screens.chat.domain.useCases.ObserveMessagesUseCase
import com.example.sawaapplication.screens.chat.domain.useCases.SendMessageUseCase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val getLastMessageWithSenderUseCase: GetLastMessageWithSenderUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUserId = authRepository.getCurrentUserId()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _senderInfo = MutableStateFlow<Map<String, ChatUserInfo>>(emptyMap())
    val senderInfo: StateFlow<Map<String, ChatUserInfo>> get() = _senderInfo

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
    }

    fun observeMessages(communityId: String) {
        viewModelScope.launch {
            try {
                observeMessagesUseCase(communityId).collect {
                    _messages.value = it
                }
            } catch (e: Exception) {
                _error.value = "Failed to load messages: ${e.message}"
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

    private val _lastMessageMap = MutableStateFlow<Map<String, Pair<String, ChatUserInfo>>>(emptyMap())
    val lastMessageMap: StateFlow<Map<String, Pair<String, ChatUserInfo>>> = _lastMessageMap

    fun fetchLastMessageForCommunity(communityId: String) {
        viewModelScope.launch {
            Log.d("ChatViewModel", "Fetching last message for community: $communityId")
            val result = getLastMessageWithSenderUseCase(communityId)
            if (result != null) {
                Log.d("ChatViewModel", "Fetched message: ${result.first}, sender: ${result.second.name}")
                _lastMessageMap.value = _lastMessageMap.value + (communityId to result)
            } else {
                Log.w("ChatViewModel", "No last message found for $communityId")
            }
        }
    }
}

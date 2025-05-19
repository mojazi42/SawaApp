package com.example.sawaapplication.screens.chat.presentation.vmModels

import android.content.Context
import android.net.Uri
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
import com.example.sawaapplication.screens.chat.domain.useCases.GetCommunityMembersUseCase
import com.example.sawaapplication.screens.chat.domain.useCases.GetLastMessageWithSenderUseCase
import com.example.sawaapplication.screens.chat.domain.useCases.GetSenderInfoUseCase
import com.example.sawaapplication.screens.chat.domain.useCases.MarkMessagesAsReadUseCase
import com.example.sawaapplication.screens.chat.domain.useCases.ObserveMessagesUseCase
import com.example.sawaapplication.screens.chat.domain.useCases.SendMessageUseCase
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val getLastMessageWithSenderUseCase: GetLastMessageWithSenderUseCase,
    private val authRepository: AuthRepository,
    private val fetchUnreadMessagesUseCase: FetchUnreadMessagesUseCase,
    private val markMessagesAsReadUseCase: MarkMessagesAsReadUseCase,
    private val getSenderInfoUseCase: GetSenderInfoUseCase,
    private val getCommunityMembersUseCase: GetCommunityMembersUseCase
) : ViewModel() {

    // --- existing state flows ---
    val currentUserId = authRepository.getCurrentUserId()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _senderInfo = MutableStateFlow<Map<String, ChatUserInfo>>(emptyMap())
    val senderInfo: StateFlow<Map<String, ChatUserInfo>> get() = _senderInfo

    private val _communityMembers = MutableStateFlow<List<ChatUserInfo>>(emptyList())
    val communityMembers: StateFlow<List<ChatUserInfo>> = _communityMembers

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _dataLoaded = MutableStateFlow(false)
    val dataLoaded: StateFlow<Boolean> = _dataLoaded

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // --- new: grouped UI items by date ---
    private val _uiMessages = MutableStateFlow<List<UiMessageItem>>(emptyList())
    val uiMessages: StateFlow<List<UiMessageItem>> = _uiMessages

    private val _chatMedia = MutableStateFlow<List<String>>(emptyList()) // URLs
    val chatMedia: StateFlow<List<String>> = _chatMedia

    // --- community members fetch (unchanged) ---
    fun fetchCommunityMembers(communityId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val members = getCommunityMembersUseCase(communityId)
                _communityMembers.value = members
            } catch (e: Exception) {
                _communityMembers.value = emptyList()
                _error.value = "Error fetching community members: ${e.message}"
            } finally {
                checkIfDataLoaded()
            }
        }
    }

    // --- observe raw messages and also group them by date ---
    fun observeMessages(communityId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                currentUserId?.let { userId ->
                    observeMessagesUseCase(communityId, userId).collect { newMessages ->
                        _messages.value = newMessages
                        _uiMessages.value = groupMessagesWithDate(newMessages)

                        // Extract media
                        val mediaUrls = newMessages.mapNotNull { it.imageUrl.takeIf { it.isNotBlank() } }
                        _chatMedia.value = mediaUrls

                        markMessagesAsRead(communityId, userId)
                    }
                }
            } catch (e: Exception) {
                _messages.value = emptyList()
                _error.value = "Error fetching messages: ${e.message}"
            } finally {
                checkIfDataLoaded()
            }
        }
    }

    private fun checkIfDataLoaded() {
        if (_communityMembers.value.isNotEmpty() && _messages.value.isNotEmpty()) {
            _loading.value = false
            _dataLoaded.value = true
        } else {
            _loading.value = false
        }
    }

    // --- send message ---

    fun sendMessage(
        communityId: String,
        messageText: String,
        senderId: String,
        imageUrl: Uri? = null
    ) {
        val message =
            Message(senderId = senderId, text = messageText, imageUrl = imageUrl?.toString() ?: "")
        viewModelScope.launch {
            sendMessageUseCase(communityId, message)
        }
    }

    // --- last message map  ---
    private val _lastMessageMap =
        MutableStateFlow<Map<String, Pair<String, ChatUserInfo>>>(emptyMap())
    val lastMessageMap: StateFlow<Map<String, Pair<String, ChatUserInfo>>> = _lastMessageMap

    fun fetchLastMessageForCommunity(communityId: String) {
        viewModelScope.launch {
            Log.d("ChatViewModel", "Fetching last message for community: $communityId")
            getLastMessageWithSenderUseCase(communityId)?.let { result ->
                _lastMessageMap.value = _lastMessageMap.value + (communityId to result)
            }
        }
    }

    // --- unread counts (unchanged) ---
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

    // --- sender info (unchanged) ---
    fun fetchSenderInfo(userId: String) {
        viewModelScope.launch {
            if (!_senderInfo.value.containsKey(userId)) {
                getSenderInfoUseCase(userId)?.let {
                    _senderInfo.value = _senderInfo.value + (userId to it)
                }
            }
        }
    }

    // --- Helper: Groups messages with date headers ---
    private fun groupMessagesWithDate(messages: List<Message>): List<UiMessageItem> {
        val result = mutableListOf<UiMessageItem>()
        var lastDate: String? = null

        messages.forEach { msg ->
            val date = formatDate(msg.createdAt)
            if (date != lastDate) {
                result.add(UiMessageItem(MessageType.DATE_HEADER, date = date))
                lastDate = date
            }
            result.add(UiMessageItem(MessageType.MESSAGE, message = msg))
        }

        return result
    }

    private fun formatDate(timestamp: Timestamp?): String {
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return timestamp
            ?.toDate()
            ?.let { sdf.format(it) }
            ?: ""
    }

    // --- UI helper types ---
    enum class MessageType {
        DATE_HEADER,
        MESSAGE
    }

    data class UiMessageItem(
        val type: MessageType,
        val message: Message? = null,
        val date: String? = null
    )
}

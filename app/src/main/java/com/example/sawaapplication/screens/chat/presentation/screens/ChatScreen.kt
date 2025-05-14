package com.example.sawaapplication.screens.chat.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sawaapplication.screens.chat.domain.model.Message
import com.example.sawaapplication.screens.chat.presentation.vmModels.ChatViewModel
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel
import com.example.sawaapplication.screens.profile.vm.ProfileViewModel
import com.example.sawaapplication.ui.screenComponent.CustomTextField
import com.example.sawaapplication.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChatScreen(
    communityId: String,
    navController: NavController
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val profileImageUrl by profileViewModel.profileImageUrl.collectAsState()

    val communityViewModel: CommunityViewModel = hiltViewModel()
    val communityDetails by communityViewModel.communityDetail.collectAsState()
    val communityName = communityDetails?.name

    val chatViewModel: ChatViewModel = hiltViewModel()
    val currentUserId = chatViewModel.currentUserId

    val messages by chatViewModel.messages.collectAsState()
    val senderInfoMap by chatViewModel.senderInfo.collectAsState()

    var messageText by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    // Observe messages and community details
    LaunchedEffect(communityId) {
        chatViewModel.observeMessages(communityId)
        communityViewModel.fetchCommunityDetail(communityId)
    }

    // Fetch sender info once when messages change
    LaunchedEffect(messages) {
        val senderIds = messages.map { it.senderId }.distinct()
        senderIds.forEach { senderId ->
            chatViewModel.fetchSenderInfo(senderId)
        }
    }

    // Auto-scroll to last message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    LaunchedEffect(Unit) {
        chatViewModel.currentCommunityIdInView = communityId
    }

    DisposableEffect(Unit) {
        onDispose {
            chatViewModel.currentCommunityIdInView = null
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Outlined.ArrowCircleLeft,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            communityName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 21.dp)
                )
            }
        }

        // Chat messages
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState,
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(messages) { message ->
                val senderInfo = senderInfoMap[message.senderId]
                ChatBubble(
                    message = message,
                    isCurrentUser = message.senderId == currentUserId,
                    image = senderInfo?.image,
                    userName = senderInfo?.name
                )
            }
        }

        // Input Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            CustomTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                label = "Type your message"
            )
            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(firstOrange)
                    .clickable {
                        if (messageText.isNotBlank()) {
                            profileImageUrl?.let { profileImage ->
                                if (currentUserId != null) {
                                    chatViewModel.sendMessage(
                                        communityId = communityId,
                                        messageText = messageText,
                                        senderId = currentUserId,
                                    )
                                }
                            }
                            messageText = ""
                        }
                    }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ChatBubble(message: Message, isCurrentUser: Boolean, image: String?, userName: String?) {
    // Format the timestamp
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = message.createdAt?.let {
        dateFormat.format(it.toDate()) // Convert Timestamp to Date
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isCurrentUser) {
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
            Column(
                horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start,
                modifier = Modifier.widthIn(max = 290.dp)
            ) {
                if (userName != null) {
                    Text(
                        userName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = if (isCurrentUser) TextAlign.End else TextAlign.Start,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCurrentUser) Color(0xFFFD7B4D) else Color(0xFFD6D6D6))
                        .padding(8.dp)
                ) {
                    Text(
                        message.text,
                        color =
                        if (isCurrentUser) white else black,
                        fontSize = 14.sp,
                    )
                    // Show the formatted time below the message
                    formattedTime?.let {
                        Text(
                            text = it,
                            color = if (isCurrentUser) white else black,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .align(if (isCurrentUser) Alignment.Start else Alignment.End ),
                        )
                    }
                }
            }

            if (isCurrentUser) {
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


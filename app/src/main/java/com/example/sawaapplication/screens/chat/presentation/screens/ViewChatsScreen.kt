package com.example.sawaapplication.screens.chat.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sawaapplication.screens.chat.presentation.vmModels.ChatViewModel
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel
import com.example.sawaapplication.ui.theme.firstOrange

@Composable
fun ViewChatsScreen(
    navController: NavController,
) {
    val chatViewModel: ChatViewModel = hiltViewModel()
    val communityViewModel: CommunityViewModel = hiltViewModel()

    val communityList by communityViewModel.createdCommunities.collectAsState()
    val loading by communityViewModel.loading.collectAsState()
    val error by communityViewModel.error.collectAsState()

    val lastMessages by chatViewModel.lastMessageMap.collectAsState()
    val currentUserId = chatViewModel.currentUserId


    // Fetch communities
    LaunchedEffect(currentUserId) {
        currentUserId?.let {
            communityViewModel.fetchCreatedCommunities(it)
        }
    }

    // Fetch last messages when communities are fetched
    LaunchedEffect(communityList) {
        communityList.forEach { community ->
            chatViewModel.fetchLastMessageForCommunity(community.id)
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        when {
            loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            error != null -> {
                Text(
                    text = error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            communityList.isEmpty() -> {
                Text(
                    text = "You have not joined any communities.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(communityList) { community ->
                        val unreadCount =
                            chatViewModel.unreadCount.collectAsState().value[community.id] ?: 0

                        // Fetch unread count when each community appears
                        LaunchedEffect(community.id) {
                            chatViewModel.fetchUnreadMessages(community.id, currentUserId ?: "")
                        }

                        val lastMessagePair = lastMessages[community.id]
                        val lastMessageText = lastMessagePair?.first ?: "No messages yet"
                        val senderName = lastMessagePair?.second?.name ?: "Unknown"

                        ChatCard(
                            imageUrl = community.image,
                            title = community.name,
                            lastMessage = "$senderName: $lastMessageText",
                            unreadCount = unreadCount,
                            modifier = Modifier.clickable {
                                chatViewModel.markMessagesAsRead(community.id, currentUserId ?: "")
                                navController.navigate("chat/${community.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatCard(
    imageUrl: String,
    title: String,
    lastMessage: String,
    unreadCount: Int = 0,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lastMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = (-4).dp)
                        .background(firstOrange, shape = CircleShape)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = unreadCount.toString(),
                        color = MaterialTheme.colorScheme.background,
                        fontSize = 10.sp,
                    )
                }
            }
        }
        HorizontalDivider(color = Gray, thickness = 0.5.dp)
    }
}

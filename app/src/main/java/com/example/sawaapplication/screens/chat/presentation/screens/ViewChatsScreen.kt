package com.example.sawaapplication.screens.chat.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sawaapplication.navigation.Screen
import com.example.sawaapplication.screens.chat.presentation.vmModels.ChatViewModel
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel

@Composable
fun ViewChatsScreen(
    navController: NavController,
    communityId: String,
) {
    val chatViewModel : ChatViewModel = hiltViewModel()
    val communityViewModel: CommunityViewModel = hiltViewModel()

    val communityDetail by communityViewModel.communityDetail.collectAsState()
    val lastMessages by chatViewModel.lastMessageMap.collectAsState()

    LaunchedEffect(communityId) {
        communityViewModel.fetchCommunityDetail(communityId)
        chatViewModel.fetchLastMessageForCommunity(communityId)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(15) { // only one community for now
            communityDetail?.let { community ->
                val lastMessagePair = lastMessages[communityId]
                val lastMessageText = lastMessagePair?.first ?: "No messages yet"
                val senderName = lastMessagePair?.second?.name ?: "Unknown"

                ChatCard(
                    imageUrl = community.image,
                    title = community.name,
                    lastMessage = "$senderName: $lastMessageText",
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Chat.route)
                    }
                )
            }
        }
    }
}

@Composable
fun ChatCard(
    imageUrl: String,
    title: String,
    lastMessage: String,
    modifier: Modifier = Modifier
) {

    Column (
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()

    ) {
        Row(
            modifier = Modifier.padding(12.dp),
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

            Column {
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
    }
    HorizontalDivider(color = Gray, thickness = 0.5.dp)
}

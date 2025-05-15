package com.example.sawaapplication.screens.chat.presentation.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    val communityViewModel: CommunityViewModel = hiltViewModel()
    val communityDetails by communityViewModel.communityDetail.collectAsState()
    val communityImage = communityDetails?.image.orEmpty()
    val communityName = communityDetails?.name.orEmpty()

    val chatViewModel: ChatViewModel = hiltViewModel()
    val currentUserId = chatViewModel.currentUserId
    val messages by chatViewModel.messages.collectAsState()
    val senderInfoMap by chatViewModel.senderInfo.collectAsState()
    val communityMembers by chatViewModel.communityMembers.collectAsState()

    val loading by chatViewModel.loading.collectAsState()
    val error by chatViewModel.error.collectAsState()

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Fetch data
    LaunchedEffect(communityId) {
        communityViewModel.fetchCommunityDetail(communityId)
        chatViewModel.fetchCommunityMembers(communityId)
        chatViewModel.observeMessages(communityId)
    }

    // Auto-scroll
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    // Fetch sender info once when messages change
    LaunchedEffect(messages) {
        val senderIds = messages.map { it.senderId }.distinct()
        senderIds.forEach { senderId -> chatViewModel.fetchSenderInfo(senderId) }
    }


    // Loading / Error
    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Scaffold(
        bottomBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(8.dp)
            ) {
                CustomTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    label = "Type your message"
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            if (messageText.isNotBlank()) {
                                chatViewModel.sendMessage(
                                    communityId = communityId,
                                    messageText = messageText,
                                    senderId = currentUserId ?: ""
                                )
                                messageText = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
        }
    ) { innerPadding ->
        // drop the top inset, keep start/end/bottom
        val start = innerPadding.calculateStartPadding(LocalLayoutDirection.current)
        val end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
        val bottom = innerPadding.calculateBottomPadding()

        Column(
            Modifier
                .fillMaxSize()
                .padding(start = start, end = end, bottom = bottom)
        ) {
            // 1. Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                AsyncImage(
                    model = communityImage,
                    contentDescription = "Community Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }

            // 2. Community name
            Text(
                text = communityName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Overlapping Avatars + Count Pill
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy((-12).dp),  // negative spacing
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Show up to 5 avatars
                items(communityMembers.take(5)) { member ->
                    AsyncImage(
                        model = member.image,
                        contentDescription = member.name,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.background,
                                shape = CircleShape
                            )
                            .clickable { navController.navigate("groupMembers/$communityId") },
                        contentScale = ContentScale.Crop
                    )
                }

                // Count Pill
                item {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp) // small gap so pill doesn’t overlap last avatar too much
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { navController.navigate("groupMembers/$communityId") }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${communityMembers.size} Members",
                            color = MaterialTheme.colorScheme.background,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }


            }
            HorizontalDivider( color = Color.Gray, thickness = 0.5.dp)

            // 4. Messages or placeholder
            if (messages.isEmpty()) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No messages yet.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
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
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: Message,
    isCurrentUser: Boolean,
    image: String?,
    userName: String?
) {
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
                        .background(
                            if (isCurrentUser) Color(0xFFFD7B4D) else Color(
                                0xFFD6D6D6
                            )
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        message.text,
                        color =
                        if (isCurrentUser) MaterialTheme.colorScheme.background else black,
                        fontSize = 14.sp,
                    )
                    // Show the formatted time below the message
                    formattedTime?.let {
                        Text(
                            text = it,
                            color = if (isCurrentUser) MaterialTheme.colorScheme.background else black,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .align(if (isCurrentUser) Alignment.Start else Alignment.End),
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


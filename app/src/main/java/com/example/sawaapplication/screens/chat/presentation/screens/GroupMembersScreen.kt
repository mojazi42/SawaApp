package com.example.sawaapplication.screens.chat.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sawaapplication.screens.chat.presentation.vmModels.ChatViewModel
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

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.MaterialTheme

import androidx.compose.ui.Alignment

import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp

import coil.compose.AsyncImage
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel

@Composable
fun GroupMembersScreen(
    communityId: String,
) {
    val communityViewModel: CommunityViewModel = hiltViewModel()
    val communityDetails by communityViewModel.communityDetail.collectAsState()
    val communityImage = communityDetails?.image.orEmpty()
    val communityName = communityDetails?.name.orEmpty()

    val chatViewModel: ChatViewModel = hiltViewModel()
    val members by chatViewModel.communityMembers.collectAsState()
    val loading by chatViewModel.loading.collectAsState()
    val error by chatViewModel.error.collectAsState()

    // Fetch when screen appears
    LaunchedEffect(communityId) {
        communityViewModel.fetchCommunityDetail(communityId)
        chatViewModel.fetchCommunityMembers(communityId)
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            loading -> {
                CircularProgressIndicator()
            }

            error != null -> {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            else -> {
                Column(Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        AsyncImage(
                            model = communityImage,
                            contentDescription = "Community",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = communityName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${members.size} members",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

/*
                    // Horizontal scroll of avatars + names
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(members) { member ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .widthIn(min = 64.dp)
                                    .clickable { */
/* Maybe show member detail? *//*
 }
                            ) {
                                AsyncImage(
                                    model = member.image,
                                    contentDescription = member.name,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.surface,
                                            CircleShape
                                        ),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = member.name ?: "Unknown",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
*/
                    Spacer(Modifier.height(16.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    Spacer(Modifier.height(16.dp))

                    // Vertical list of all members
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(members) { member ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = member.image,
                                    contentDescription = member.name,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = member.name ?: "Unknown",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


package com.example.sawaapplication.screens.communities.presentation.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel
import com.example.sawaapplication.ui.theme.*

data class PostUiModel(
    val username: String,
    val userAvatarUrl: String = "",
    val postImageUrl: String = ""
)

data class CommunityUiState(
    val logoUrl: String,
    val communityName: String,
    val membersCount: String,
    val communityDescription: String,
    val posts: List<PostUiModel>
)

private val FakeCommunityUiState = CommunityUiState(
    logoUrl = "",
    communityName = "Saudi Innovation",
    membersCount = "2.5M",
    communityDescription = "This community fosters innovation across Saudi Arabia...",
    posts = listOf(
        PostUiModel(
            "@mohammed1",
            userAvatarUrl = "https://i.pravatar.cc/150?img=1",
            postImageUrl = ""
        ),
        PostUiModel(
            "@ahmed2",
            userAvatarUrl = "https://i.pravatar.cc/150?img=2",
            postImageUrl = "https://images.unsplash.com/photo-1593642634367-d91a135587b5"
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    communityId: String,
    viewModel: CommunityViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onClick: () -> Unit,
    navController: NavHostController
) {
    val uiState = FakeCommunityUiState
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Posts", "Events")
    LaunchedEffect(communityId) {
        Log.d("DEBUG", "CommunityScreen launched with id: $communityId")
        viewModel.fetchCommunityDetail(communityId)
    }
    val communityDetail by viewModel.communityDetail.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {},
                windowInsets = WindowInsets(0)

            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                containerColor = PrimaryOrange,
                contentColor = white,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Post")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(24.dp))
                AsyncImage(
                    model = communityDetail?.image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(16.dp))
                communityDetail?.let {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = black
                    )
                }
                Text(
                    text = "${communityDetail?.members?.size ?: 0} Members",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray
                )
                Spacer(Modifier.height(12.dp))
                communityDetail?.let {
                    Text(
                        text = it.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { /* TODO: Handle Join */ },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Join Community", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(Modifier.height(24.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = white,
                    indicator = { positions ->
                        TabRowDefaults.Indicator(
                            Modifier
                                .tabIndicatorOffset(positions[selectedTab])
                                .height(3.dp),
                            color = PrimaryOrange
                        )
                    }
                ) {
                    tabs.forEachIndexed { i, title ->
                        Tab(
                            selected = selectedTab == i,
                            onClick = { selectedTab = i },
                            text = {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (selectedTab == i) black else Gray
                                )
                            }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            if (selectedTab == 0) {
                items(uiState.posts) { post ->
                    PostCard(post)
                }
            } else {
                item {
                    EventCard(navController = navController, communityId = communityId)
                }
            }
        }
    }
}
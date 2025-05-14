package com.example.sawaapplication.screens.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.home.presentation.screens.component.CustomTabRow
import com.example.sawaapplication.screens.home.presentation.screens.component.EventCard
import com.example.sawaapplication.screens.home.presentation.screens.component.PostCard
import com.example.sawaapplication.screens.home.presentation.vmModels.HomeViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableIntStateOf

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Posts", "My Events")

    Box(modifier = Modifier.fillMaxSize()) {

        when (selectedTabIndex) {
            0 -> PostsTab(viewModel)
            1 -> MyEventsTab() // implement if needed
        }

        // Top transparent tab row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.TopCenter)
                .zIndex(1f)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
        ) {
            CustomTabRow(
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )
        }
    }
}

@Composable
fun PostsTab(viewModel: HomeViewModel) {
    val posts by viewModel.posts.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val communityNames by viewModel.communityNames.collectAsState()
    val userDetails by viewModel.userDetails.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllPosts()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

            error != null -> Text(
                text = error ?: "Unknown error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )

            else ->
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = 72.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 56.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                items(posts) { post ->
                    val communityName = communityNames[post.communityId] ?: "Unknown"
                    val (userName, userImage) = userDetails[post.userId] ?: ("Unknown" to "")
                    PostCard(
                        post,
                        communityName,
                        userName,
                        userImage,
                        onClick = {},
                        onLikeClick = { viewModel.likePost(post) })
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MyEventsTab() {
    val eventsCount = 10
    // Just track join states — this is safe
    val joinedStates = remember { mutableStateListOf(*Array(eventsCount) { true }) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(eventsCount) { index ->
            // Composable call inside the proper scope
//            val imagePainter = painterResource(id = R.drawable.first)

            EventCard(
                image = "",
                community = "Saudi Innovation Community",
                title = "Fine art between past and present",
                description = "World Art Day, which falls on April 15, celebrates artists and their contributions...",
                location = "Madina",
                time = "16 Feb 25 • 06:00 PM-10:00 PM",
                participants = 12,
                joined = joinedStates[index],
                onJoinClick = { joinedStates[index] = !joinedStates[index] },
                showCancelButton = true,
                modifier = Modifier.padding(
                    top = if (index == 0) integerResource(id = R.integer.homeScreenTopPadding).dp else 0.dp
                )
            )
        }
    }
}
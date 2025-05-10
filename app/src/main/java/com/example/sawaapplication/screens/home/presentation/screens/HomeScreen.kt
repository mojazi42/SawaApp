package com.example.sawaapplication.screens.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.home.presentation.screens.component.CustomTabRow
import com.example.sawaapplication.screens.home.presentation.screens.component.EventCard
import com.example.sawaapplication.screens.home.presentation.screens.component.PostCard

@Composable
fun HomeScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Posts", "My Events")

    Box(modifier = Modifier.fillMaxSize()) {

        // Scrollable content
        when (selectedTabIndex) {
            0 -> PostsTab()
            1 -> MyEventsTab()
        }

        // Transparent floating Tab Row on top
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
fun PostsTab() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(15) { index ->
            PostCard(
                community = "Coffee Community",
                name = "Shouq Albdrani",
                username = "shouq.bd1",
                time = "8h",
                text = "Coffee Lovers: You can only bring one item to a remote island to help you perfect your coffee ritual. What do you bring? Grinder? French Press? Beans? Tell me why! #CoffeeTal",
                likes = 0,
                profileImage = painterResource(id = R.drawable.first),
                onClick = { },
                modifier = Modifier
                    .padding(
                        top = if (index == 0) integerResource(id = R.integer.homeScreenTopPadding).dp else 0.dp,
                        bottom = integerResource(id = R.integer.homeScreenBottomPadding).dp
                    )
            )
            HorizontalDivider(color = Gray, thickness = 0.5.dp)
        }
    }
}

@Composable
fun MyEventsTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(10) { index ->
            var isJoined by remember { mutableStateOf(true) }
            EventCard(
                image = painterResource(id = R.drawable.first),
                community = "Saudi Innovation Community",
                title = "Fine art between past and present",
                description = "World Art Day, which falls on April 15, celebrates artists and their contributions...",
                location = "Madinah",
                time = "16 Feb 25 • 06:00 PM-10:00 PM",
                //joined = isJoined,
                participants = 12,
                //onJoinClick = { isJoined = !isJoined },
                modifier = Modifier
                    .padding(
                        top = if (index == 0) integerResource(id = R.integer.homeScreenTopPadding).dp  else 0.dp,
                    )
            )
        }
    }
}

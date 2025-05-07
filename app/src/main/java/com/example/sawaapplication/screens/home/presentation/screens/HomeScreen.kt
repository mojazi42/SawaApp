package com.example.sawaapplication.screens.home.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.home.presentation.screens.component.CustomTabRow
import com.example.sawaapplication.screens.home.presentation.screens.component.EventCard
import com.example.sawaapplication.screens.home.presentation.screens.component.PostCard

@Composable
fun HomeScreen(
    navController: NavController
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val searchText by remember {  mutableStateOf("") }
    val tabs = listOf("Posts", "My Events")

    Column(modifier = Modifier.fillMaxSize()) {
        CustomTabRow(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it }

        )

        OutlinedTextField(
            value = searchText,
            onValueChange = {  },
            placeholder = { Text("Search") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )

        when (selectedTabIndex) {
            0 -> PostsTab()
            1 -> MyEventsTab()
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
                onClick = {  }
            )

        }
    }
}

@Composable
fun MyEventsTab() {
    LazyColumn {
        items(2) {
            var isJoined by remember { mutableStateOf(true) }
            EventCard(
                image = painterResource(id = R.drawable.first),
                community = "Saudi Innovation Community",
                title = "Fine art between past and present",
                description = "World Art Day, which falls on April 15, celebrates artists and their contributions...",
                location = "Madinah",
                time = "16 Feb 25 • 06:00 PM-10:00 PM",
                joined = isJoined,
                participants = 12,
                onJoinClick = { isJoined = !isJoined }
            )
        }
    }
}


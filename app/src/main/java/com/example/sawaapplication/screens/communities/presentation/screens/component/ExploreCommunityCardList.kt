package com.example.sawaapplication.screens.communities.presentation.screens.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sawaapplication.R

@Composable
fun ExploreCommunityCardList(communities: List<String>) {

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(communities.size) { index ->
            var isJoined by remember { mutableStateOf(false) }
            CommunityCard(
                communityName = "Saudi Community",
                communityMember = 2500000,
                communityImage = painterResource(id = R.drawable.saudi_logo),
                joinButton = {
                    JoinButton(
                        isJoined = isJoined,
                        onClick = { isJoined = !isJoined }
                    )
                },
                onClick = {
                    // Handle card click
                }
            )

            if (index < 5) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }
        }
    }
}
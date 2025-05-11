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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.screens.communities.domain.model.Community
import androidx.compose.foundation.lazy.items

@Composable
fun ExploreCommunityCardList(
    communities: List<Community>,
    onCommunityClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(communities, key = { it.id }) { community ->
            var isJoined by remember { mutableStateOf(false) }

            CommunityCard(
                communityName = community.name,
                communityMember = community.members.size,
                communityImage = rememberAsyncImagePainter(community.image),
                joinButton = {
                    JoinButton(
                        isJoined = isJoined,
                        onClick = { isJoined = !isJoined }
                    )
                },
                onClick = {
                    onCommunityClick(community.id)
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )
        }
    }
}
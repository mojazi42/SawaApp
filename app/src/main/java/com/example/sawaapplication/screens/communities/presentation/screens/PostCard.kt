package com.example.sawaapplication.screens.communities.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sawaapplication.ui.theme.Gray
import com.example.sawaapplication.ui.theme.black
import com.example.sawaapplication.ui.theme.white

@Composable
fun PostCard(post: PostUiModel) {
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(21) }

    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(integerResource(R.integer.postCardElevation).dp)
    ) {
        Column {
            Column(Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = post.userAvatarUrl,
                        contentDescription = null,
                        modifier = Modifier.size(integerResource(R.integer.postCardImageSize).dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(integerResource(R.integer.smallerSpace).dp))
                    Text(post.username, style = MaterialTheme.typography.bodyMedium, color = black)
                }

                Spacer(Modifier.height(integerResource(R.integer.smallerSpace).dp))

                if (post.postImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = post.postImageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(integerResource(R.integer.postCardImageHeight).dp)
                            .clip(RoundedCornerShape(integerResource(R.integer.chatRoundedCornerShape).dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "This is a text-only post by ${post.username}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = black
                    )
                }
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    isLiked = !isLiked
                    likeCount += if (isLiked) 1 else -1
                }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) MaterialTheme.colorScheme.error else Gray
                    )
                }
                Text("$likeCount", style = MaterialTheme.typography.bodySmall, color = Gray)
            }
        }
    }
}
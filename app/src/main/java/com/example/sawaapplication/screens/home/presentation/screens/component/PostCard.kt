package com.example.sawaapplication.screens.home.presentation.screens.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.profile.vm.ProfileViewModel

@Composable
fun PostCard(
    community: String,
    name: String,
    username: String,
    time: String,
    text: String,
    likes: Int,
    profileImage: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(likes) }

    val profileViewModel: ProfileViewModel = hiltViewModel()
    val imageUrl by profileViewModel.profileImageUrl.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(integerResource(id = R.integer.smallSpace).dp)
    ) {
        Text(
            text = community,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(start = integerResource(id=R.integer.homeCommunityNamePostPadding).dp)
        )

        Spacer(modifier = Modifier.height(integerResource(id = R.integer.smallerSpace).dp))

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .padding(start = integerResource(id = R.integer.extraSmallSpace).dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = if (imageUrl != null)
                    rememberAsyncImagePainter(imageUrl)
                else
                    painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Profile image",
                contentScale = ContentScale.Crop,

                modifier = Modifier
                    .clip(CircleShape)
                    .size(integerResource(id = R.integer.homePostProfileImage).dp)
            )

            Spacer(modifier = Modifier.width(integerResource(id=R.integer.smallSpace).dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),

                    ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(integerResource(id = R.integer.smallerSpace).dp))
                        Text(
                            text = "@$username",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = "• $time",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(integerResource(id=R.integer.extraSmallSpace).dp))

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    //maxLines = 3,
                    //overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = integerResource(id = R.integer.smallerSpace).dp)
                    .align(Alignment.Bottom),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        isLiked = !isLiked
                        likeCount = if (isLiked) likeCount + 1 else likeCount - 1
                    }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.Gray,
                        modifier = Modifier.size(integerResource(id = R.integer.homeScreenIconSize).dp)
                    )
                    Spacer(modifier = Modifier.width(integerResource(id = R.integer.extraSmallSpace).dp))
                    Text(
                        text = "$likeCount",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

package com.example.sawaapplication.screens.communities.presentation.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sawaapplication.R
import com.example.sawaapplication.navigation.Screen
import com.example.sawaapplication.screens.post.domain.model.PostUiModel
import com.example.sawaapplication.ui.screenComponent.CustomConfirmationDialog
import com.example.sawaapplication.ui.theme.black
import com.example.sawaapplication.ui.theme.white
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PostCard(
    post: PostUiModel,
    currentUserId: String,
    onImageClick: (String) -> Unit,
    onLikeClick: (PostUiModel) -> Unit,
    onDeleteClick: (() -> Unit)? = null,  // Optional delete callback
    navController: NavController
) {
    val isLiked = currentUserId in post.likedBy
    val isOwnedByCurrentUser = currentUserId == post.userId
    var showDeleteDialog by remember { mutableStateOf(false) }

    val formattedDate = remember(post.createdAt) {
        try {
            val parser = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
            val date = parser.parse(post.createdAt)
            val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            formatter.format(date ?: Date())
        } catch (e: Exception) {
            "Unknown date"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(integerResource(R.integer.cardRoundedCornerShape).dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(integerResource(R.integer.postCardElevation).dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // User row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (post.userAvatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = post.userAvatarUrl,
                        contentDescription = "User Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(integerResource(R.integer.asyncImageSize).dp)
                            .clip(CircleShape)
                            .clickable {
                                if (post.userId == currentUserId) {
                                    navController.navigate(Screen.Profile.route)
                                } else {
                                    navController.navigate(Screen.UserAccount.createRoute(userId = post.userId))
                                }
                            },
                    )
                }
                Spacer(modifier = Modifier.width(integerResource(R.integer.smallerSpace).dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.username,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.UserAccount.createRoute(userId = post.userId))
                        }
                    )
                    Text(
                        text = "${stringResource(R.string.postedOn)} $formattedDate",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Post content
            if (post.content.isNotBlank()) {
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            // Post image
            if (post.postImageUrl.isNotBlank()) {
                AsyncImage(
                    model = post.postImageUrl,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onImageClick(post.postImageUrl) },
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
            }

            // Likes section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onLikeClick(post) }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isLiked) "Unlike" else "Like",
                            tint = if (isLiked) Color.Red else Color.Gray
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (post.likes == 1) "1 ${stringResource(R.string.like)}" else "${post.likes} ${stringResource(R.string.likes)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (isOwnedByCurrentUser && onDeleteClick != null) {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Post",
                            tint = Color.Gray
                        )
                    }
                }
            }

            if (showDeleteDialog) {
                CustomConfirmationDialog(
                    message = stringResource(R.string.areYouSurePost),
                    onDismiss = { showDeleteDialog = false },
                    onConfirm = {
                        showDeleteDialog = false
                        onDeleteClick?.invoke()  // call delete lambda without arguments
                    }
                )
            }
        }
    }
}

package com.example.sawaapplication.screens.home.presentation.screens.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.R
import com.google.firebase.Timestamp


@Composable
fun EventCard(
    image: String,
    community: String,
    title: String,
    description: String,
    location: String,
    time: String,
    date: String,
    participants: Int,
    joined: Boolean,
    eventTimestamp: Timestamp?,
    onJoinClick: () -> Unit,
    showCancelButton: Boolean = false,
    joinedUsers: List<String> = emptyList(),
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    fun isEventExpired(eventTime: Timestamp): Boolean {
        val eventMillis = eventTime.toDate().time
        val expiryMillis = eventMillis + 60 * 60 * 1000 // Add 1 hour
        val currentMillis = System.currentTimeMillis()

        return currentMillis > expiryMillis
    }

    val isExpired = remember(eventTimestamp) {
        eventTimestamp?.let { isEventExpired(it) } ?: false
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(integerResource(id = R.integer.smallerSpace).dp)
            .clickable { onClick() },

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = RoundedCornerShape(integerResource(id = R.integer.homeScreenRoundedCornerShape).dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = integerResource(id = R.integer.homeScreenCardElevation).dp
        ),
        border = BorderStroke(
            integerResource(R.integer.stroke).dp, MaterialTheme.colorScheme.secondaryContainer
        ),
    ) {
        Column {
            Row {
                Image(
                    painter = if (image != null)
                        rememberAsyncImagePainter(image)
                    else
                        painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Profile image",
                    contentScale = ContentScale.Crop,

                    modifier = Modifier
                        .clip(RectangleShape)
                        .clip(RoundedCornerShape(topStart = integerResource(id = R.integer.homeScreenRoundedCornerShape).dp))
                        .size(integerResource(id = R.integer.homeScreenEventImageSize).dp)
                )

                Spacer(modifier = Modifier.width(integerResource(id = R.integer.smallSpace).dp))

                Column(
                    modifier = Modifier
                        .padding(top = integerResource(id = R.integer.smallSpace).dp)
                        .weight(1f)
                ) {
                    Text(
                        text = community,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                    )
                    Spacer(modifier = Modifier.height(integerResource(R.integer.spacer).dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(integerResource(id = R.integer.smallerSpace).dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = integerResource(R.integer.titleSize).sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    JoinButton(
                        joined = joined,
                        onJoinClick = onJoinClick,
                        showCancel = showCancelButton,
                        isExpired=isExpired
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = integerResource(id = R.integer.smallSpace).dp,
                        vertical = integerResource(id = R.integer.smallerSpace).dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(integerResource(id = R.integer.homeScreenIconSize).dp)
                    )
                    Text(
                        text = location,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )

                }

                Row(
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Icon(
                        Icons.Outlined.Timer,
                        contentDescription = "Time",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(integerResource(id = R.integer.homeScreenIconSize).dp)
                    )
                    Text(
                        text = "${date}•${time}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Participants",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(integerResource(id = R.integer.homeScreenIconSize).dp)
                    )
                    Spacer(modifier = Modifier.width(integerResource(id = R.integer.extraSmallSpace).dp))
                    Text(
                        text = "${participants}/${joinedUsers.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}


@Composable
fun JoinButton(
    joined: Boolean,
    onJoinClick: () -> Unit,
    showCancel: Boolean = false,
    isExpired: Boolean
) {
    val isCancelVisible = showCancel && joined
    val isButtonEnabled = !isExpired && !joined
    // disabled : !joined && (limit full)
    Button(
        onClick = onJoinClick,
        enabled = isButtonEnabled,
        shape = RoundedCornerShape(30),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isCancelVisible -> Color(0xFFEAEAEA)
                joined -> Color(0xFFEAEAEA)
                else -> MaterialTheme.colorScheme.primary
            },
            contentColor = when {
                isCancelVisible -> Color.Gray
                joined -> Color.Gray
                else -> Color.White
            }
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        modifier = Modifier
            .defaultMinSize(minHeight = 1.dp)
            .height(28.dp)
    ) {
        Text(
            text = when {
                isExpired -> "Finished"
                isCancelVisible -> "Leave"
                joined -> "Joined"
                else -> "Join"
            },
            style = MaterialTheme.typography.labelSmall
        )
    }
}
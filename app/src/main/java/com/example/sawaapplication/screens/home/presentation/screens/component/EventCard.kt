package com.example.sawaapplication.screens.home.presentation.screens.component

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.R
import com.google.firebase.Timestamp
import com.example.sawaapplication.screens.event.domain.model.Event

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
    isEditable: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    canJoinEvents: Boolean = true,
) {
    val context = LocalContext.current

    fun isEventExpired(eventTime: Timestamp): Boolean {
        val eventMillis = eventTime.toDate().time
        val expiryMillis = eventMillis + 60 * 60 * 1000 // Add 1 hour
        val currentMillis = System.currentTimeMillis()

        return currentMillis > expiryMillis
    }

    fun isEventFull(participantsLimit: Int, currentJoined: Int): Boolean {
        return currentJoined >= participantsLimit
    }

    val isFull = isEventFull(participants, joinedUsers.size)
    val isExpired = remember(eventTimestamp) {
        eventTimestamp?.let { isEventExpired(it) } ?: false
    }
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(integerResource(id = R.integer.smallerSpace).dp)
            .clickable {
                if (canJoinEvents) {
                    onClick()
                } else {
                    Toast.makeText(
                        context,
                        "You need to join the community until you can join the event",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = RoundedCornerShape(integerResource(id = R.integer.homeScreenRoundedCornerShape).dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = integerResource(id = R.integer.homeScreenCardElevation).dp
        ),
        border = BorderStroke(
            integerResource(R.integer.stroke).dp,
            MaterialTheme.colorScheme.secondaryContainer
        ),
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Image
                Image(
                    painter = if (image.isNotEmpty())
                        rememberAsyncImagePainter(image)
                    else
                        painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Event image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RectangleShape)
                        .clip(
                            RoundedCornerShape(
                                topStart = integerResource(id = R.integer.homeScreenRoundedCornerShape).dp
                            )
                        )
                        .size(integerResource(id = R.integer.homeScreenEventImageSize).dp)
                )

                Spacer(modifier = Modifier.width(integerResource(id = R.integer.smallSpace).dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = integerResource(id = R.integer.smallSpace).dp)
                ) {
                    Text(
                        text = community,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
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
                        isExpired = isExpired,
                        isFull = isFull,
                        canJoinEvents = canJoinEvents,
                    )
                }

                // Overflow menu
                if (isEditable) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Top)
                            .padding(top = 4.dp, end = 4.dp)
                    ) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    expanded = false
                                    onEditClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", color = Color.Red) },
                                onClick = {
                                    expanded = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }

            // Bottom info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = integerResource(id = R.integer.smallSpace).dp,
                        vertical = integerResource(id = R.integer.smallerSpace).dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(integerResource(id = R.integer.homeScreenIconSize).dp)
                    )
                    Text(
                        text = location,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Timer,
                        contentDescription = "Time",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(integerResource(id = R.integer.homeScreenIconSize).dp)
                    )
                    Text(
                        text = "$date•$time",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
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
                        text = "${joinedUsers.size}/$participants",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    if (joinedUsers.size >= participants) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Full",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
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
    isExpired: Boolean,
    isFull: Boolean,
    canJoinEvents: Boolean = true,
) {
    val isCancelVisible = showCancel && joined
    val isEventFull = isFull && !joined
    val isLocked = !canJoinEvents && !joined

    val isButtonEnabled = when {
        isExpired -> false
        isEventFull -> false
        isLocked -> false
        isCancelVisible -> true
        joined -> false
        else -> true
    }

    Button(
        onClick = onJoinClick,
        enabled = isButtonEnabled,
        shape = RoundedCornerShape(30),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isCancelVisible -> Color(0xFFEAEAEA)
                joined -> Color(0xFFEAEAEA)
                isLocked -> Color(0xFFEAEAEA)
                else -> MaterialTheme.colorScheme.primary
            },
            contentColor = when {
                isCancelVisible -> Color.Gray
                joined -> Color.Gray
                isEventFull -> Color.Gray
                isLocked -> Color.Gray
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
                isLocked -> "🔒 Locked"
                isEventFull -> "Join"
                isExpired -> "Finished"
                isCancelVisible -> "Leave"
                joined -> "Joined"
                else -> "Join"
            },
            style = MaterialTheme.typography.labelSmall
        )
    }
}
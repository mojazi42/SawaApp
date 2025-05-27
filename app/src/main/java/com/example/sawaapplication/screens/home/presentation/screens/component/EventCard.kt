package com.example.sawaapplication.screens.home.presentation.screens.component

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sawaapplication.R
import com.google.firebase.Timestamp
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.example.sawaapplication.ui.theme.errorColor
import com.example.sawaapplication.ui.theme.thirdOrange


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
    onCommunityClick: ((String) -> Unit)? = null,
    communityId: String,

    ) {
    val context = LocalContext.current
    val joinedCount = joinedUsers.size

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
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = integerResource(id = R.integer.smallerSpace).dp,
                horizontal = integerResource(id = R.integer.smallerSpace).dp
            )
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

        shape = RoundedCornerShape(integerResource(id = R.integer.cardRoundedCornerShape).dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column {
            //Top image
            Box {
                AsyncImage(
                    model = image,
                    contentDescription = "Event image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(integerResource(id = R.integer.eventImageHeight).dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = integerResource(id = R.integer.cardRoundedCornerShape).dp,
                                topEnd = integerResource(id = R.integer.cardRoundedCornerShape).dp
                            )
                        )
                )
                if (isExpired) {
                    Badge(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(integerResource(id = R.integer.smallerSpace).dp),
                        containerColor = errorColor,
                    ) {
                        Text(
                            stringResource(id = R.string.expired),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                } else if (isFull) {
                    Badge(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(integerResource(id = R.integer.smallerSpace).dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            stringResource(id = R.string.full),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(Modifier.height(integerResource(id = R.integer.smallerSpace).dp))

            Column(Modifier.padding(horizontal = integerResource(id = R.integer.mediumSpace).dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Community tag
                    AssistChip(
                        onClick = { onCommunityClick?.invoke(communityId) },
                        label = { Text(community) },
                        leadingIcon = { Icon(Icons.Outlined.Group, contentDescription = null) },
                        shape = RoundedCornerShape(integerResource(id = R.integer.RoundedCornerShape).dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                    )
                    if (isEditable) {
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Options")
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(id = R.string.edit)) },
                                    onClick = {
                                        menuExpanded = false
                                        onEditClick()
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            stringResource(id = R.string.delete),
                                            color = Color.Red
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onDeleteClick()
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(integerResource(id = R.integer.smallerSpace).dp))

                // Title & description
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(integerResource(id = R.integer.extraSmallSpace).dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(integerResource(id = R.integer.smallSpace).dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // location & time
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(integerResource(id = R.integer.mediumSpace).dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoPill(
                            icon = Icons.Outlined.LocationOn,
                            text = location,
                            maxTextWidth = integerResource(id = R.integer.eventInfoPillWidth).dp
                        )
                        InfoPill(
                            icon = Icons.Outlined.Timer,
                            text = "$date • $time",
                        )
                    }
                }

                Spacer(Modifier.height(integerResource(id = R.integer.smallSpace).dp))

                //join/leave button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    JoinButton(
                        joined = joined,
                        onJoinClick = onJoinClick,
                        showCancel = showCancelButton,
                        isExpired = isExpired,
                        isFull = isFull,
                        canJoinEvents = canJoinEvents
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Participants count
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Participants",
                            tint = if (joinedUsers.size >= participants) thirdOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(integerResource(id = R.integer.homeScreenIconSize).dp)
                        )
                        Spacer(modifier = Modifier.width(integerResource(id = R.integer.extraSmallSpace).dp))

                        Text(
                            text = "$participants/${joinedUsers.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (joinedUsers.size >= participants) thirdOrange else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(integerResource(id = R.integer.smallerSpace).dp))
            }
        }
    }
}

@Composable
private fun InfoPill(
    icon: ImageVector,
    text: String,
    maxTextWidth: Dp = Dp.Unspecified,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
                shape = RoundedCornerShape(integerResource(id = R.integer.cardRoundedCornerShape).dp)
            )
            .padding(
                horizontal = integerResource(id = R.integer.smallerSpace).dp,
                vertical = integerResource(id = R.integer.extraSmallSpace).dp
            )
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(integerResource(id = R.integer.eventInfoPillIcon).dp)
        )
        Spacer(Modifier.width(integerResource(id = R.integer.extraSmallSpace).dp))
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = if (maxTextWidth != Dp.Unspecified)
                Modifier.widthIn(max = maxTextWidth)
            else
                Modifier
        )
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
                isCancelVisible || joined || isLocked -> Color(0xFFEAEAEA)
                else -> MaterialTheme.colorScheme.primary
            },
            contentColor = when {
                isCancelVisible || joined || isEventFull || isLocked -> Color.Gray
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
                isLocked   -> "🔒 Locked"
                isExpired  -> "Finished"
                isEventFull-> "Full"
                isCancelVisible -> "Leave"
                joined     -> "Joined"
                else       -> "Join"
            },
            style = MaterialTheme.typography.labelSmall
        )
    }
}
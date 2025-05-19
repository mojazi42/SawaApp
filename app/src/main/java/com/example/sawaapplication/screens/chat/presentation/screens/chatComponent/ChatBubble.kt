package com.example.sawaapplication.screens.chat.presentation.screens.chatComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sawaapplication.screens.chat.domain.model.Message
import com.example.sawaapplication.ui.theme.black
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChatBubble(
    message: Message,
    isCurrentUser: Boolean,
    image: String?,
    userName: String?,
    unreadCount: Int,
    onImageClick: (String) -> Unit
) {
    // Format the timestamp
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = message.createdAt?.let {
        dateFormat.format(it.toDate()) // Convert Timestamp to Date
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top, // avatar stays top-aligned
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isCurrentUser) {
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
            Column(
                horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start,
                modifier = Modifier.widthIn(max = 290.dp)
            ) {
                if (userName != null) {
                    Text(
                        userName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = if (isCurrentUser) TextAlign.End else TextAlign.Start,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                    )
                }

                Column(
                    horizontalAlignment = if (isCurrentUser) Alignment.Start else Alignment.End,
                    modifier = Modifier
                        .widthIn(max = 250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isCurrentUser) Color(0xFFFD7B4D) else Color(0xFFD6D6D6)
                        )
                        .padding(8.dp)
                ) {
                    message.imageUrl.takeIf { it.isNotBlank() }?.let { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Sent image",
                            modifier = Modifier
                                .widthIn(max = 200.dp)
                                .heightIn(max = 200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onImageClick(imageUrl) },
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(
                        message.text,
                        color = if (isCurrentUser) MaterialTheme.colorScheme.background else black,
                        fontSize = 14.sp,
                    )

                    formattedTime?.let {
                        Text(
                            text = it,
                            color = if (isCurrentUser) MaterialTheme.colorScheme.background else black,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Text(
                        text = if (unreadCount == 0) "Read by all" else "Not read by $unreadCount",
                        fontSize = 10.sp,
                        color = if (isCurrentUser) MaterialTheme.colorScheme.background else Color.DarkGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                }
            }

            if (isCurrentUser) {
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


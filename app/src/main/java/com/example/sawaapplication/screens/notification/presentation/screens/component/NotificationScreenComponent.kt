package com.example.sawaapplication.screens.notification.presentation.screens.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun NotificationCard(
    name: String,
    time: String,
    action: String,
    profileImage: Painter,
    postImage: Painter,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Profile Image (left)
            Image(
                painter = profileImage,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(20.dp))

            // Notification Content (center)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = action,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Post Image (right)
            Image(
                painter = postImage,
                contentDescription = "Post Picture",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RectangleShape)
            )

        }
    }
}

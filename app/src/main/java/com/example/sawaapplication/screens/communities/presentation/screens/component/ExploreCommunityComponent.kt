package com.example.sawaapplication.screens.communities.presentation.screens.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CommunityCard(
    communityName: String,
    communityMember : Int,
    communityImage: Painter,
    joinButton : @Composable () -> Unit,
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
            // Community Image
            Image(
                painter = communityImage,
                contentDescription = "Community page",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(20.dp))

            // Community Content (center)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = communityName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "${communityMember} Members",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light
                )

            }
            joinButton()

        }

    }


}

@Composable
fun JoinButton(
    isJoined: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isJoined) Color.White else Color(0xFFFF6B3D), // Orange
            contentColor = if (isJoined) Color(0xFFFF6B3D) else Color.White
        ),
        border = if (isJoined) BorderStroke(1.dp, Color(0xFFFF6B3D)) else null,
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        elevation = null
    ) {
        Icon(
            imageVector = if (isJoined) Icons.Default.CheckCircle else Icons.Default.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (isJoined) "Joined" else "Join",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}


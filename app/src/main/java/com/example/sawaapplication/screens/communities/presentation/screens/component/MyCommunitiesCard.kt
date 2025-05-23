package com.example.sawaapplication.screens.communities.presentation.screens.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.communities.domain.model.Community

@Composable
fun MyCommunitiesCard(
    community: Community,
    onClick: () -> Unit
) {
    val memberCount = community.members.size // Count members from list size

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(integerResource(id=R.integer.cardHeightMyCommunity).dp)
            .padding(vertical = integerResource(id = R.integer.extraSmallSpace).dp)
            .clickable { onClick() }, // Added click behavior to trigger navigation
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = RoundedCornerShape(integerResource(id=R.integer.CardRoundedCornerShape).dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = integerResource(id = R.integer.communityCardElevation).dp
        ),
        border = BorderStroke(
            1.dp, MaterialTheme.colorScheme.secondaryContainer
        ),
    ) {
        Column(
            modifier = Modifier.padding(integerResource(id = R.integer.smallerSpace).dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(integerResource(id = R.integer.smallerSpace).dp))

            // Load community image
            AsyncImage(
                model = community.image,
                contentDescription = community.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(integerResource(id=R.integer.AvatarImage).dp)
                    .clip(CircleShape)

            )
            Spacer(modifier = Modifier.height(integerResource(id = R.integer.smallerSpace).dp))

            Text(
                text = "$memberCount members",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )

            Text(
                text = community.name,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = integerResource(id = R.integer.smallerTextFontSize).sp,
            )

            Spacer(modifier = Modifier.height(integerResource(id = R.integer.smallerSpace).dp))

            community.description?.takeIf { it.isNotBlank() }?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 2, // Show only 2 lines
                    overflow = TextOverflow.Ellipsis // Add "..." if description too long

                )
            }
        }
    }
}

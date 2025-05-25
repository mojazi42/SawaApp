package com.example.sawaapplication.screens.profile.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sawaapplication.screens.profile.domain.model.Badge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sawaapplication.screens.profile.presentation.vm.ProfileViewModel

@Composable
fun BadgeRow(badges: List<Badge>) {

    val profileViewModel: ProfileViewModel = hiltViewModel()

    val defs by profileViewModel.badgeDefinitions.collectAsState()
    val earnedIds = badges.map { it.id }
    val orderedIds = listOf("3_days", "1_week", "2_weeks", "3_weeks", "1_month")
    val orderedDefs = orderedIds.mapNotNull { id -> defs.find { it.id == id } }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        items(orderedDefs) { badgeDef ->
            val isEarned = earnedIds.contains(badgeDef.id)
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 12.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isEarned) {
                        AsyncImage(
                            model = badgeDef.iconUrl,
                            contentDescription = badgeDef.name,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Text("🔒", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = badgeDef.name,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isEarned) LocalContentColor.current else Color.Gray
                    )
                }
            }
        }
    }
}

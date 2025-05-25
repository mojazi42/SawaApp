package com.example.sawaapplication.screens.profile.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sawaapplication.screens.profile.domain.model.Badge

@Composable
fun CompactBadgeRow(
    definitions: List<Badge>,
    awarded: List<Badge>,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    spacing: Dp = 8.dp
) {
    val earnedIds = awarded.map { it.id }
    val orderedIds = listOf("3_days", "1_week", "2_weeks", "3_weeks", "1_month")
    val orderedDefs = orderedIds.mapNotNull { id -> definitions.find { it.id == id } }

    LazyRow(modifier = modifier) {
        items(orderedDefs) { badgeDef ->
            val isEarned = earnedIds.contains(badgeDef.id)
            Box(
                modifier = Modifier
                    .padding(end = spacing)
            ) {
                if (isEarned) {
                    AsyncImage(
                        model = badgeDef.iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}

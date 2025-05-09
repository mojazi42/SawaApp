package com.example.sawaapplication.screens.communities.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sawaapplication.ui.theme.PrimaryOrange

@Composable
fun EventCard() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Upcoming Events",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = PrimaryOrange
        )
        Spacer(Modifier.height(16.dp))
        Text(
            " Hackathon 2024 - May 12th\n📍 Riyadh Innovation Hub",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "🚀 Startup Pitch - June 3rd\n📍 Jeddah Business Center",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        //  You can replace with LazyColumn of EventCards
    }
}
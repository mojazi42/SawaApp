@file:JvmName("EventCardKt")

package com.example.sawaapplication.screens.communities.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.home.presentation.screens.component.EventCard
import com.example.sawaapplication.ui.theme.PrimaryOrange
@Composable
fun EventCardScreen(navController: NavHostController, communityId: String) {
    val events = listOf(
        EventData(
            image = painterResource(id = R.drawable.ic_launcher_background),
            community = "Tech Community",
            title = "Hackathon 2024",
            description = "Join us for a 48-hour coding marathon and compete for amazing prizes.",
            location = "Riyadh",
            time = "16 Feb 25",
            participants = 120
        ),
        EventData(
            image = painterResource(id = R.drawable.ic_launcher_background),
            community = "Startup Circle",
            title = "Startup Pitch",
            description = "Pitch your startup idea to investors and network with professionals.",
            location = "Jeddah",
            time = "June 3",
            participants = 85
        )
    )

    val joinStates = remember { mutableStateListOf(*Array(events.size) { false }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(integerResource(R.integer.padding).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.upcomingEvents),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = PrimaryOrange
        )

        Spacer(modifier = Modifier.height(integerResource(R.integer.mediumSpace).dp))

        events.forEachIndexed { index, event ->
            EventCard(
                image = event.image,
                community = event.community,
                title = event.title,
                description = event.description,
                location = event.location,
                time = event.time,
                participants = event.participants,
                joined = joinStates[index],
                onJoinClick = { joinStates[index] = !joinStates[index] },
                showCancelButton = false
            )
            Spacer(modifier = Modifier.height(integerResource(R.integer.smallerSpace).dp))
        }
    }
}
data class EventData(
    val image: Painter,
    val community: String,
    val title: String,
    val description: String,
    val location: String,
    val time: String,
    val participants: Int
)

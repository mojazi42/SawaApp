package com.example.sawaapplication.screens.event.presentation.screens

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.event.presentation.vmModels.FetchEventViewModel
import com.example.sawaapplication.ui.theme.PrimaryOrange
import com.google.firebase.firestore.GeoPoint
import java.util.Locale

@Composable
fun EventViewScreen(communityId: String) {
    val context = LocalContext.current
    val viewModel: FetchEventViewModel = hiltViewModel()
    val events by viewModel.events.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // 🔁 Trigger loading of events once when communityId changes
    LaunchedEffect(communityId) {
        viewModel.loadEvents(communityId)
    }

    Column(modifier = Modifier.fillMaxSize()
        .padding(integerResource(R.integer.padding).dp)) {
        Text(
            text = "Saved Events",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = PrimaryOrange,
            modifier = Modifier.padding(bottom = integerResource(R.integer.padding).dp)
        )

        when {
            loading -> {
                CircularProgressIndicator()
            }
            error != null -> {
                Text("Error loading events: $error", color = Color.Red)
            }
            else -> {
                LazyColumn {
                    items(events) { event ->
                        val cityName = context.getCityNameFromGeoPoint(event.location)

                        EventViewCard(
                            image = event.imageUri,
                            title = event.title,
                            description = event.description,
                            location = cityName, // Use the city name here
                            participants = event.memberLimit
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
fun Context.getCityNameFromGeoPoint(geoPoint: GeoPoint): String {
    return try {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)
        val address = addresses?.getOrNull(0)

        if (address != null) {
            listOfNotNull(
                address.thoroughfare,
            ).joinToString(", ")
        } else {
            "No Location Set"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Unknown Location"
    }
}



@Composable
fun EventViewCard(
    image: String,
    title: String,
    description: String,
    location: String,
    participants: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Load image using Coil
            AsyncImage(
                model = image,
                contentDescription = "Event Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Location: $location")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Participants: $participants")
        }
    }
}

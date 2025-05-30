package com.example.sawaapplication.screens.event.presentation.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.AddLocation
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel
import com.example.sawaapplication.screens.home.presentation.vmModels.HomeViewModel
import com.example.sawaapplication.utils.getCityNameFromGeoPoint
import com.google.common.collect.Iterables.size
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailScreen(
    communityId: String,
    eventId: String,
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val communityViewModel: CommunityViewModel = hiltViewModel()
    val community by communityViewModel.communityDetail.collectAsState()
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl
    val event = viewModel.getEventById(eventId)

    LaunchedEffect(Unit) {
        viewModel.loadEvents(communityId)
        communityViewModel.fetchCommunityDetail(communityId)
    }


        if (event == null || community == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }


        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = integerResource(R.integer.paddingEventScreen).dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(integerResource(R.integer.screenTopSpace).dp + 8.dp))

                AsyncImage(
                    model = event.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(integerResource(R.integer.round).dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.height(16.dp))

                // Date & Members Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoColumn(
                        icon = Icons.Default.CalendarToday,
                        text = formatEventDate(event.date),
                        modifier = Modifier.weight(1f)
                    )
                    InfoColumn(
                        iconRes = R.drawable.members,
                        text = "${event.joinedUsers.size}",
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(8.dp))

                // Location
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoColumn(
                        icon = Icons.Filled.LocationOn,
                        text = context.getCityNameFromGeoPoint(event.location)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Spacer(Modifier.height(48.dp))

                Button(
                    onClick = {
                        val geoUri = Uri.parse("google.navigation:q=${event.latitude},${event.longitude}")
                        val intent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        startActivity(context, intent, null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.viewLocation),color=MaterialTheme.colorScheme.background)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Map, contentDescription = null,tint= MaterialTheme.colorScheme.background)

                }

                Spacer(Modifier.height(48.dp))
            }

            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(integerResource(R.integer.screenTopSpace).dp)
                    .background(MaterialTheme.colorScheme.background)
                    .align(Alignment.TopCenter)
            ) {
                Text(
                    text =community?.name ?: stringResource(R.string.loading),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp),
                    maxLines = 1
                )

                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer { scaleX = if (isRtl) -1f else 1f }
                    )
                }
            }
        }
    }

@Composable
fun InfoColumn(
    icon: ImageVector? = null,
    iconRes: Int? = null,
    text: String,
    maxTextWidth: Dp = Dp.Unspecified,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        icon?.let {
            Icon(imageVector = it, contentDescription = null, modifier = Modifier.size(18.dp))
        }
        iconRes?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = if (maxTextWidth != Dp.Unspecified)
                Modifier.widthIn(max = maxTextWidth)
            else
                Modifier
        )
    }
}
fun formatEventDate(rawDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("d MMM yyyy • h:mm a", Locale.getDefault())
        val date = inputFormat.parse(rawDate)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        rawDate
    }
}
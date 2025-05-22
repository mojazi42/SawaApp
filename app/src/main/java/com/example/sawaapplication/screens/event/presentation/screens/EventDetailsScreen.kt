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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.outlined.AddLocation
import androidx.compose.material3.Button
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
    LaunchedEffect(Unit) {
        viewModel.loadEvents(communityId)
        communityViewModel.fetchCommunityDetail(communityId)
    }

    val event = viewModel.getEventById(eventId)
    val context = LocalContext.current

    if (event == null) {
        Text(
            "Event not found",
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Fixed header box (stays at the top)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(integerResource(R.integer.screenTopSpace).dp)
                .background(MaterialTheme.colorScheme.background)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Back",
                    modifier = Modifier.size(integerResource(R.integer.backIconSize).dp)
                )
            }
            Text(
                text = community?.name ?: stringResource(R.string.loading),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = integerResource(R.integer.smallerSpace).dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(integerResource(R.integer.paddingEventScreen).dp)
                .verticalScroll(state = rememberScrollState())
        ) {

            Spacer(Modifier.height(integerResource(R.integer.hugeSpace).dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = integerResource(R.integer.paddingEventScreen).dp)
            ) {
                AsyncImage(
                    model = event.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(integerResource(R.integer.round).dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(integerResource(R.integer.smallerSpace).dp))
            val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("d MMMM yy  h:mma", Locale.getDefault())

            val formattedDate = try {
                val date = inputFormat.parse(event.date)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                event.date
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                .padding(horizontal = integerResource(R.integer.paddingEventScreen).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date section
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )

                // Members section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = size(event.joinedUsers).toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.width(integerResource(R.integer.extraSmallSpace).dp))
                    Icon(
                        painter = painterResource(R.drawable.members),
                        contentDescription = "members icon",
                        modifier = Modifier.size(integerResource(R.integer.membersIconSize).dp)
                    )
                }

                // Location section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = context.getCityNameFromGeoPoint(event.location),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.width(integerResource(R.integer.extraSmallSpace).dp))
                    Icon(
                        imageVector = Icons.Outlined.AddLocation,
                        contentDescription = "location icon",
                        modifier = Modifier.size(integerResource(R.integer.mediumSpace).dp)
                    )
                }
            }

            Spacer(Modifier.height(integerResource(R.integer.extraLargeSpace).dp))

            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = integerResource(R.integer.paddingEventScreen).dp)
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = integerResource(R.integer.paddingEventScreen).dp)
            )

            Spacer(modifier = Modifier.height(integerResource(R.integer.hugeSpace).dp))

            Button(
                onClick = {
                    val geoUri =
                        Uri.parse("google.navigation:q=${event.latitude},${event.longitude}")
                    val intent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    startActivity(context, intent, null)
                },
                modifier = Modifier
                    .width(230.dp)
                    .height(40.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.viewLocation), color = Color.White)
            }
        }
    }

}
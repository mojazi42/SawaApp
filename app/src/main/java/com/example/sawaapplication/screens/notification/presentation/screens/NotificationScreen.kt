package com.example.sawaapplication.screens.notification.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sawaapplication.screens.notification.presentation.screens.component.NotificationCard
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import com.example.sawaapplication.screens.notification.presentation.viewmodels.NotificationViewModel
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
@Composable
fun NotificationScreen(
    navController: NavController,
    notificationViewModel: NotificationViewModel = hiltViewModel(),
) {
    val notifications = notificationViewModel.notifications.collectAsState().value

    LaunchedEffect(Unit) {
        notificationViewModel.markNotificationsAsSeen()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // or use your integerResource if needed
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        if (notifications.isEmpty()) {
            Text(text = "No notifications.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(notifications.size) { index ->
                    val notification = notifications[index]
                    NotificationCard(
                        name = "System Notification",
                        time = SimpleDateFormat("hh:mm a").format(notification.timestamp),
                        action = notification.message,
                        profileImage = null,
                        postImage = null,
                        onClick = {}
                    )
                }
            }
        }
    }
}

package com.example.sawaapplication.navigation.topBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.R
import com.example.sawaapplication.navigation.Screen
import com.example.sawaapplication.screens.notification.presentation.viewmodels.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        },
        navigationIcon = {
            navigationIcon?.invoke()
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier.padding(
            horizontal = integerResource(id = R.integer.extraSmallSpace).dp,
            vertical = integerResource(id = R.integer.mediumSpace).dp,
        )
    )
}

@Composable
fun getTopBar(
    currentRoute: String?,
    navController: NavController,
    imageUrl: String?,
    notificationViewModel: NotificationViewModel = hiltViewModel()
): (@Composable () -> Unit)? {

    val hasUnread = notificationViewModel.hasUnreadNotifications.collectAsState().value

    LaunchedEffect(currentRoute) {
        notificationViewModel.checkUnreadStatus()
    }

    return when (currentRoute) {
        Screen.Community.route -> {
            { TopBar(title = stringResource(id = R.string.myCommunities)) }
        }

        Screen.Explore.route -> {
            { TopBar(title = stringResource(id = R.string.exploreCommunity)) }
        }

        Screen.Home.route -> {
            {
                TopBar(title = "") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = integerResource(id = R.integer.mediumSpace).dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = imageUrl?.let { rememberAsyncImagePainter(it) }
                                ?: painterResource(id = R.drawable.ic_launcher_background),
                            contentDescription = "Profile image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(integerResource(id = R.integer.topBarIconSize).dp)
                                .clickable {
                                    navController.navigate(Screen.Profile.route)
                                }
                        )

                        val logoPainter = painterResource(
                            id = R.drawable.applogo
                        )

                        Image(
                            painter = logoPainter,
                            contentDescription = "App Icon",
                            modifier = Modifier.size(integerResource(id = R.integer.topBarIconSize).dp)
                        )

                        IconButton(onClick = {
                            notificationViewModel.markNotificationsAsSeen()
                            navController.navigate(Screen.Notification.route) }) {
                            Box {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )

                                if (hasUnread) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .align(Alignment.TopEnd)
                                            .offset(x = 2.dp, y = (-2).dp)
                                            .background(Color.Red, CircleShape)
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }

        Screen.Notification.route -> {
            { TopBar(title = stringResource(id = R.string.notification)) }
        }

        Screen.EditProfile.route -> {
            {
                TopBar(
                    title = stringResource(id = R.string.editProfile),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowCircleLeft,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(integerResource(id = R.integer.bottomBarIconSize).dp)
                            )
                        }
                    }
                )
            }

        }

        else -> null
    }
}

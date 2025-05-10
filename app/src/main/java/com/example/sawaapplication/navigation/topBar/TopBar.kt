package com.example.sawaapplication.navigation.topBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.R
import com.example.sawaapplication.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = actions,

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier.padding(
            horizontal = integerResource(id = R.integer.extraSmallSpace).dp,
            vertical = integerResource(id = R.integer.mediumSpace).dp,
        ),
    )
}

@Composable
fun getTopBar(
    currentRoute: String?,
    navController: NavController,
    imageUrl: String?
): (@Composable () -> Unit)? {
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
                            id = if (isSystemInDarkTheme())
                                R.drawable.white_app_icon
                            else
                                R.drawable.applogo
                        )

                        Image(
                            painter = logoPainter,
                            contentDescription = "App Icon",
                            modifier = Modifier.size(integerResource(id = R.integer.topBarIconSize).dp)
                        )

                        IconButton(onClick = { navController.navigate(Screen.Notification.route) }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        Screen.Notification.route -> {
            { TopBar(title = stringResource(id = R.string.notification)) }
        }

        Screen.EditProfile.route -> {
            { TopBar(title = stringResource(id = R.string.editProfile)) }
        }

        else -> null
    }
}

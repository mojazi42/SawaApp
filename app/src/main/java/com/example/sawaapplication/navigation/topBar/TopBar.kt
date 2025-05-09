package com.example.sawaapplication.navigation.topBar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sawaapplication.R
import com.example.sawaapplication.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    screen: Screen,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = when (screen) {
                    is Screen.Explore -> stringResource(R.string.exploreCommunity)
                    is Screen.Notification -> stringResource(R.string.notification)
                    is Screen.Community -> stringResource(R.string.myCommunities)
                    is Screen.EditProfile -> stringResource(R.string.editProfile)
                    else -> ""
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier.padding(
            horizontal = integerResource(id = R.integer.extraSmallSpace).dp,
            vertical = integerResource(id = R.integer.mediumSpace).dp,
        ),
    )
}

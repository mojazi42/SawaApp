package com.example.sawaapplication.screens.notification.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.notification.presentation.screens.component.NotificationCard

@Composable
fun NotificationScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(integerResource(R.integer.profilePadding).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(integerResource(R.integer.topSpacing).dp))

        Text(
            text = stringResource(id = R.string.notification),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = integerResource(R.integer.textSize2).sp
        )

        Spacer(Modifier.height(integerResource(R.integer.belowTitle).dp))

        NotificationCards()

    }

}

@Composable
fun NotificationCards() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(6) { index ->
            NotificationCard(
                name = "Ethar Alrehaili",
                time = "09:55 AM",
                action = "Liked your post",
                profileImage = painterResource(id = R.mipmap.ic_cat1),
                postImage = painterResource(id = R.mipmap.ic_space1),
                onClick = { }
            )

            if (index < 5) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }
        }
    }
}

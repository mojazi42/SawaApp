package com.example.sawaapplication.screens.profile.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.profile.vm.ProfileViewModel

@Composable
fun UserAccount(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    userId: String
) {

    val selectedUser by profileViewModel.selectedUser.collectAsState()

    LaunchedEffect(userId) {
        profileViewModel.fetchUserById(userId)
    }

    selectedUser?.let { user ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(integerResource(R.integer.profilePadding).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(integerResource(R.integer.topSpacing).dp))

                Box(modifier = Modifier.size(integerResource(R.integer.photoBoxSize).dp)) {
                    Image(
                        painter = if (!user.image.isNullOrEmpty())
                            rememberAsyncImagePainter(user.image)
                        else
                            painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Profile image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(integerResource(R.integer.photoBoxSize).dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = user.name ?: "Unknown",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = user.email ?: "No email",
                    textAlign = TextAlign.Center,
                    fontSize = integerResource(R.integer.textSize1).sp
                )

                Spacer(Modifier.height(integerResource(R.integer.pioSpacer).dp))

                Text(
                    text = stringResource(id = R.string.aboutMe),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = integerResource(R.integer.textSize2).sp
                )

                Text(
                    text = user.aboutMe ?: "",
                    textAlign = TextAlign.Center,
                    fontSize = integerResource(R.integer.textSize2).sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    } ?: run {
        // Loading or empty state
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Loading user info...")
        }
    }
}

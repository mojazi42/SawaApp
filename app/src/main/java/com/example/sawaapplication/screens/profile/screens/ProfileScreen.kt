package com.example.sawaapplication.screens.profile.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.authentication.presentation.vmModels.LogOutViewModel
import com.example.sawaapplication.screens.profile.vm.ProfileViewModel
import com.example.sawaapplication.ui.screenComponent.GradientButton

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userName by profileViewModel.userName.collectAsState()
    val userEmail by profileViewModel.userEmail.collectAsState()
    val aboutMe by profileViewModel.aboutMe.collectAsState()

    var readOnly by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userName ?: "") }
    var editedEmail by remember { mutableStateOf(userEmail ?: "") }
    var editedAboutMe by remember { mutableStateOf(aboutMe ?: "") }

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userName) {
        if (userName != null) editedName = userName.toString()
    }
    LaunchedEffect(userEmail) {
        if (userEmail != null) editedEmail = userEmail.toString()
    }
    LaunchedEffect(aboutMe) {
        if (aboutMe != null) editedAboutMe = aboutMe.toString()
    }

    val logOutViewModel: LogOutViewModel = hiltViewModel()
    Box(modifier = Modifier.fillMaxSize()) {
        //Edit profile
        IconButton(
            onClick = {
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings icon"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(integerResource(R.integer.profilePadding).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(integerResource(R.integer.topSpacing).dp))

            Box(
                modifier = Modifier
                    .size(integerResource(R.integer.photoBoxSize).dp)
            ) {
                //Image
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .clip(CircleShape),
                )

                //Edit icon
                IconButton(
                    onClick = {
                        readOnly = readOnly
                    },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Create,
                        contentDescription = "Edit icon",
                    )
                }
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text("Edit Profile", color = Color.Black)
                    },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = editedName,
                                onValueChange = { editedName = it },
                                label = { Text("Enter your name", color = Color.Gray) },
                                textStyle = TextStyle(color = Color.Black)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = editedAboutMe,
                                onValueChange = { editedAboutMe = it },
                                label = { Text("About me", color = Color.Gray) },
                                textStyle = TextStyle(color = Color.Black)
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            profileViewModel.updateName(editedName)
                            profileViewModel.updateAboutMe(editedAboutMe)
                            showDialog = false
                        }) {
                            Text("Save", color = Color.Black)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            editedName = userName?:""
                            editedAboutMe = aboutMe?:""
                            showDialog = false
                        }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    },
                    containerColor = Color.White
                )
            }

            //Name
            Text(
                text = editedName,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(integerResource(R.integer.zero).dp)
            )

            //Email
            Text(
                text = editedEmail,
                textAlign = TextAlign.Center,
                fontSize = integerResource(R.integer.textSize1).sp,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(integerResource(R.integer.zero).dp)
            )

            Spacer(Modifier.height(integerResource(R.integer.pioSpacer).dp))

            // About Me Section
            Text(
                text = stringResource(id = R.string.aboutMe),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = integerResource(R.integer.textSize2).sp
            )

            // About me content
            Text(
                text = editedAboutMe,
                textAlign = TextAlign.Center,
                fontSize = integerResource(R.integer.textSize2).sp,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(integerResource(R.integer.zero).dp)
            )

            GradientButton(
                onClick = {
                    logOutViewModel.preformLogOut(navController)
                },
                text = "Log Out",
                modifier = Modifier.padding(top = integerResource(id = R.integer.largerSpace).dp)
            )

        }
    }
}

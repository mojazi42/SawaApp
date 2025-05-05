package com.example.sawaapplication.screens.profile.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.example.sawaapplication.navigation.Screen
import com.example.sawaapplication.screens.profile.vm.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavController, profileViewModel: ProfileViewModel = hiltViewModel()) {
    val userName by profileViewModel.userName.collectAsState()
    val userEmail by profileViewModel.userEmail.collectAsState()

    var readOnly by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userName ?: "") }
    var editedEmail by remember { mutableStateOf(userEmail ?: "") }

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
                onClick = { readOnly = !readOnly },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Filled.Create,
                    contentDescription = "Edit icon",
                )
            }
        }
        //Name
        TextField(
            value = editedName,
            onValueChange = { editedName = it },
            readOnly = readOnly,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier
                .wrapContentSize()
                .padding(integerResource(R.integer.zero).dp),
            colors = TextFieldDefaults.colors(

                // Transparent background
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,

                // Remove underline
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )

        // Email TextField
        TextField(
            value = editedEmail,
            onValueChange = { editedEmail = it },
            readOnly = readOnly,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = integerResource(R.integer.textSize1).sp
            ),
            modifier = Modifier
                .wrapContentSize()
                .padding(integerResource(R.integer.zero).dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )

        Spacer(Modifier.height(integerResource(R.integer.pioSpacer).dp))

        // About Me Section
        Text(
            text = stringResource(id = R.string.aboutMe),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = integerResource(R.integer.textSize2).sp
        )
        TextField(
            value = "Hi! I like to explore and try new things",
            onValueChange = {},
            readOnly = readOnly,
            modifier = Modifier
                .wrapContentSize()
                .padding(integerResource(R.integer.zero).dp),
            singleLine = false,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = integerResource(R.integer.textSize2).sp
            ),

            colors = TextFieldDefaults.colors(

                // Transparent background
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,

                // Remove underline
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )
        // This text is for navigation to the login screen and will be removed after implementing Log out.
        Text(
            text = stringResource(id = R.string.Login),
            fontSize = integerResource(id = R.integer.smallText).sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                navController.navigate(Screen.Login)

            }
        )
    }
}

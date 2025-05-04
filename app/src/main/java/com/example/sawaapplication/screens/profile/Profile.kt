package com.example.sawaapplication.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sawaapplication.R

@Composable
fun ProfileScreen() {

    var readOnly by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))

        Box(
            modifier = Modifier
                .size(124.dp)
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
            value = "Shahad Aldawsari",
            onValueChange = {},
            readOnly = readOnly,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.
            wrapContentSize()
//                .height(50.dp)
                .padding(0.dp),
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

        //Email
        Text(
           "TestingEmail@gmail.com",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(Modifier.height(48.dp))

        //Pio
        Text("About me:",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        TextField(
            value = "Hi! I like to explore and try new things",
            onValueChange = {},
            readOnly = readOnly,
            modifier = Modifier.
            wrapContentSize()
                .padding(0.dp),
            singleLine = false,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 20.sp
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
    }
}
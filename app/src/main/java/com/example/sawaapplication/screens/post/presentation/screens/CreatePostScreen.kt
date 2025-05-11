package com.example.sawaapplication.screens.post.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.post.presentation.vmModels.CreatePostViewModel
import com.example.sawaapplication.ui.screenComponent.CustomTextField

@Composable
fun CreatePostScreen(
    navController: NavController,
    communityId : String
) {
    val viewModel: CreatePostViewModel = hiltViewModel()
    val imageUri by remember { derivedStateOf { viewModel.imageUri } }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.imageUri = uri
    }

    LaunchedEffect(communityId) {
        viewModel.communityId = communityId
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(integerResource(R.integer.padding).dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        // Top Row: Cancel + Post button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(integerResource(R.integer.padding).dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            TextButton(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.cancel))
            }

            Button(
                onClick = {
                    viewModel.createPost(communityId)
                    navController.navigate("community_screen/$communityId")
                },
            ) { Text(stringResource(R.string.post)) }

        }

        // Row for Community Image + Add Photo Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(integerResource(R.integer.padding).dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Community Image
            Image(
                painter = rememberAsyncImagePainter("https://api.dicebear.com/7.x/lorelei/svg"),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Blue)
            )
            // Add Photo Icon
            Icon(
                imageVector = Icons.Outlined.AddAPhoto,
                contentDescription = "Add photo",
                modifier = Modifier
                    .clickable { imagePickerLauncher.launch("image/*") }
                    .padding(8.dp),
                tint = Color.Gray
            )
        }

        //Post Content
        OutlinedTextField(
            value = viewModel.content,
            onValueChange = { viewModel.content = it },
            placeholder = { Text("Share your Idea", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 16.dp),
            singleLine = false,
            shape = RoundedCornerShape(8.dp),
        )

        // Show selected image preview if available
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

    }
}
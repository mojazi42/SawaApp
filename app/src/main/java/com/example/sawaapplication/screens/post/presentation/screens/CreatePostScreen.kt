package com.example.sawaapplication.screens.post.presentation.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel
import com.example.sawaapplication.screens.post.presentation.vmModels.CreatePostViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    communityId: String
) {
    val context = LocalContext.current
    val createPostViewModel: CreatePostViewModel = hiltViewModel()
    val communityViewModel: CommunityViewModel = hiltViewModel()

    val communityDetails by communityViewModel.communityDetail.collectAsState()
    val communityImage = communityDetails?.image.orEmpty()
    val communityName = communityDetails?.name.orEmpty()

    val imageUri by remember { derivedStateOf { createPostViewModel.imageUri } }
    val coroutineScope = rememberCoroutineScope()

    val photoPermissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
    var showPhotoPermissionDialog by remember { mutableStateOf(false) }
    val askPhotoPermissionText = stringResource(R.string.askPhotoPermissionFromSettings)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> createPostViewModel.imageUri = uri }

    // Launch fetch
    LaunchedEffect(communityId) {
        createPostViewModel.communityId = communityId
        communityViewModel.fetchCommunityDetail(communityId)
    }

    // Show permission dialog
    if (showPhotoPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoPermissionDialog = false },
            title = { Text(stringResource(R.string.photoPermission)) },
            text = { Text(stringResource(R.string.askPhotoPermission)) },
            confirmButton = {
                TextButton(onClick = {
                    createPostViewModel.markPhotoPermissionRequested()
                    photoPermissionState.launchPermissionRequest()
                    showPhotoPermissionDialog = false
                }) {
                    Text(stringResource(R.string.allow))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPhotoPermissionDialog = false }) {
                    Text(stringResource(R.string.deny))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.cancel))
            }
            Button(onClick = {
                coroutineScope.launch {
                    val success = createPostViewModel.createPost(communityId)
                    if (success) {
                        navController.navigate("community_screen/$communityId") {
                            popUpTo("create_post/$communityId") { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        Toast.makeText(context, "Failed to create post", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text(stringResource(R.string.post))
            }
        }

        // Community Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(communityImage),
                contentDescription = "Community Image",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Text(
                text = communityName,
                modifier = Modifier.padding(start = 12.dp),
                color = Color.Black
            )
        }

        // Post Input
        OutlinedTextField(
            value = createPostViewModel.content,
            onValueChange = { createPostViewModel.content = it },
            placeholder = {
                Text(stringResource(R.string.postContentPlaceholder), color = Color.Gray)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(16.dp),
            singleLine = false,
            maxLines = 6
        )

        // Add Image Option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Outlined.AddAPhoto,
                contentDescription = "Add photo",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        if (photoPermissionState.status.isGranted) {
                            imagePickerLauncher.launch("image/*")
                        } else {
                            if (createPostViewModel.shouldRequestPhoto()) {
                                showPhotoPermissionDialog = true
                            } else {
                                Toast.makeText(
                                    context,
                                    askPhotoPermissionText,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                tint = Color.Gray
            )
        }

        // Image Preview
        imageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

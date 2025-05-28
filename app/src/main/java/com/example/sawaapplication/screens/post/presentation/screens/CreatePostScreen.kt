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
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel
import com.example.sawaapplication.screens.post.presentation.vmModels.CommunityPostsViewModel
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
    val communityPostsViewModel: CommunityPostsViewModel = hiltViewModel()
    val communityViewModel: CommunityViewModel = hiltViewModel()

    // Collect states from ViewModels
    val isLoading by communityPostsViewModel.creatingPost.collectAsState()
    val error by communityPostsViewModel.error.collectAsState()
    val success by communityPostsViewModel.success.collectAsState()

    val communityDetails by communityViewModel.communityDetail.collectAsState()
    val communityImage = communityDetails?.image.orEmpty()
    val communityName = communityDetails?.name.orEmpty()


    val imageUri = communityPostsViewModel.currentImageUri
    val content = communityPostsViewModel.currentContent

    val coroutineScope = rememberCoroutineScope()

    val photoPermissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
    var showPhotoPermissionDialog by remember { mutableStateOf(false) }
    val askPhotoPermissionText = stringResource(R.string.askPhotoPermissionFromSettings)


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> communityPostsViewModel.updateImageUri(uri) }

    // Initialize ViewModel
    LaunchedEffect(communityId) {

        communityPostsViewModel.updateCommunityId(communityId)
        communityViewModel.fetchCommunityDetail(communityId)
    }

    // Handle success state
    LaunchedEffect(success) {
        if (success) {
            Toast.makeText(context, R.string.createPostSuccess, Toast.LENGTH_SHORT).show()
            communityPostsViewModel.clearSuccess()
            navController.navigate("community_screen/$communityId") {
                popUpTo("create_post/$communityId") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // Handle error state
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            communityPostsViewModel.clearError()
        }
    }

    // Show permission dialog
    if (showPhotoPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoPermissionDialog = false },
            title = { Text(stringResource(R.string.photoPermission)) },
            text = { Text(stringResource(R.string.askPhotoPermission)) },
            confirmButton = {
                TextButton(onClick = {
                    communityPostsViewModel.markPhotoPermissionRequested()
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

    // Loading overlay
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
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
            Button(
                onClick = {
                    coroutineScope.launch {
                        communityPostsViewModel.createPost(communityId)
                    }
                },
                enabled = !isLoading && (content.isNotBlank() || imageUri != null)
            ) {
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
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

        // Post Input
        OutlinedTextField(
            value = content,

            onValueChange = { communityPostsViewModel.updateContent(it) },
            placeholder = {
                Text(stringResource(R.string.postContentPlaceholder), color = Color.Gray)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(16.dp),
            singleLine = false,
            maxLines = 6,
            enabled = !isLoading
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
                    .clickable(enabled = !isLoading) {
                        if (photoPermissionState.status.isGranted) {
                            imagePickerLauncher.launch("image/*")
                        } else {
                            if (communityPostsViewModel.shouldRequestPhoto()) {
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
                tint = if (isLoading) Color.Gray.copy(alpha = 0.5f) else Color.Gray
            )
        }

        // Image Preview
        imageUri?.let { uri ->
            Box {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Remove image button
                TextButton(
                    onClick = { communityPostsViewModel.updateImageUri(null) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text("✕", color = Color.White)
                }
            }
        }
    }
}
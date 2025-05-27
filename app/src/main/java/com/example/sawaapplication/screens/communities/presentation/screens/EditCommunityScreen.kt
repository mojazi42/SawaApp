package com.example.sawaapplication.screens.communities.presentation.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.sawaapplication.navigation.Screen
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel
import com.example.sawaapplication.ui.screenComponent.CustomConfirmationDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditCommunityScreen(
    navController: NavController,
    communityId: String,
    viewModel: CommunityViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val communityDetail by viewModel.communityDetail.collectAsState()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var category = viewModel.category
    var showDeleteDialog by remember { mutableStateOf(false) }
    val text = stringResource(R.string.photoPermissionToast)

    val communityTypes = remember {
        listOf(
            R.string.artCreativity,
            R.string.booksLiterature,
            R.string.funny,
            R.string.education,
            R.string.gaming,
            R.string.healthWellness,
            R.string.moviesTVShows,
            R.string.petsAnimals,
            R.string.sports,
            R.string.techGadgets,
            R.string.travelAdventure,
            R.string.other
        )
    }.map { stringResource(it) }

    val selectedTypeIndex = remember {
        mutableStateOf(communityTypes.indexOf(viewModel.category))
    }

    val selectedText = if (selectedTypeIndex.value >= 0) {
        communityTypes[selectedTypeIndex.value]
    } else {
        stringResource(R.string.select)
    }

    val photoPermissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
    var showPhotoPermissionDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val loading by viewModel.loading.collectAsState()
    val success by viewModel.success.collectAsState()
    val error by viewModel.error.collectAsState()
    val actionType by viewModel.actionType.collectAsState()

    LaunchedEffect(success) {
        if (success) {
            when (actionType) {
                "update" -> Toast.makeText(context, R.string.updateCommunityToast, Toast.LENGTH_SHORT).show()
                "delete" -> Toast.makeText(context, R.string.deleteCommunityToast, Toast.LENGTH_SHORT).show()
            }

            if (actionType == "delete") {
                navController.navigate("community") {
                    popUpTo("edit_community/$communityId") { inclusive = true }
                }
            } else {
                navController.popBackStack()
            }
        }
    }

    LaunchedEffect(error) {
        error?.let { message ->
            Toast.makeText(context, R.string.errorToast, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(communityId) {
        viewModel.fetchCommunityDetail(communityId)
    }

    LaunchedEffect(communityDetail) {
        name = communityDetail?.name ?: ""
        description = communityDetail?.description ?: ""
        category = communityDetail?.category ?: ""
    }

    // Photo Permission Dialog
    if (showPhotoPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoPermissionDialog = false },
            title = { Text(stringResource(R.string.photoPermissionTitle)) },/** STRING */
            text = { Text(stringResource(R.string.photoPermissionTextC)) },/** STRING */
            confirmButton = {
                TextButton(onClick = {
                    viewModel.markPhotoPermissionRequested()
                    photoPermissionState.launchPermissionRequest()
                    showPhotoPermissionDialog = false
                }) {
                    Text(stringResource(R.string.allow))/** STRING */
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoPermissionDialog = false
                }) {
                    Text(stringResource(R.string.deny))/** STRING */
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
            ) {
        item {
            // Image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (photoPermissionState.status.isGranted) {
                            imagePickerLauncher.launch("image/*")
                        } else {
                            if (viewModel.shouldRequestPhoto()) {
                                showPhotoPermissionDialog = true
                            } else {
                                Toast.makeText(
                                    context,
                                    text,/** STRING */
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "New Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(communityDetail?.image),
                        contentDescription = "Old Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit),
                    tint = Color.White
                )
            }

            Spacer(Modifier.height(16.dp))

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.communityName)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))


            CommunityTypeDropdown(
                selectedText = selectedText,
                onTypeSelected = {
                    selectedTypeIndex.value = it
                    viewModel.category = communityTypes[it]
                },
                communityTypes = communityTypes
            )


            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.updateCommunity(communityId, name, description, category, imageUri)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.update))
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = { navController.popBackStack() },
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                modifier =  Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.cancel))
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    showDeleteDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(stringResource(R.string.deleteCommunity), color = Color.White)
            }
        }
    }
    if (showDeleteDialog) {
        CustomConfirmationDialog(
            message = stringResource(R.string.areYouSureDeleteCommunity),
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteCommunity(communityId, communityDetail?.image)
            }
        )
    }
}
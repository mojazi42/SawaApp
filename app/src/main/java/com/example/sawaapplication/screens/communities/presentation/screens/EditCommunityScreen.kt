package com.example.sawaapplication.screens.communities.presentation.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel
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

    val communityTypes = remember {
        listOf(
            R.string.artCreativity,
            R.string.booksLiterature,
            R.string.funny,
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

    val loading by viewModel.loading.collectAsState()

    var showPhotoPermissionDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val success by viewModel.success.collectAsState()

    LaunchedEffect(success) {
        if (success) {
            navController.popBackStack()
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
            title = { Text("Photo Permission") },
            text = { Text("We need access to your photos so you can add an image for the event.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.markPhotoPermissionRequested()
                    photoPermissionState.launchPermissionRequest()
                    showPhotoPermissionDialog = false
                }) {
                    Text("Allow")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoPermissionDialog = false
                }) {
                    Text("Deny")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
        // Image
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable {
                    if (photoPermissionState.status.isGranted){
                        imagePickerLauncher.launch("image/*")
                    } else {
                        if (viewModel.shouldRequestPhoto()) {
                            showPhotoPermissionDialog = true
                        } else {
                            Toast.makeText(context, "Please allow photo access in settings", Toast.LENGTH_LONG).show()
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
                Image (
                    painter = rememberAsyncImagePainter(communityDetail?.image),
                    contentDescription = "Old Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Icon(
                Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        // Name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Community Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
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


        if (loading){
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.updateCommunity(communityId, name, description, category ,imageUri)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.deleteCommunity(communityId)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Delete Community", color = Color.White)
        }

    }
}
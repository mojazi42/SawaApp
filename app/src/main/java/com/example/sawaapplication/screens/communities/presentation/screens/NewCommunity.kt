package com.example.sawaapplication.screens.communities.presentation.screens

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel
import com.example.sawaapplication.ui.screenComponent.CustomTextField
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NewCommunity(navController: NavController) { //CHANGE THIS TO "CreateCommunityScreen
    val context = LocalContext.current
    val viewModel: CommunityViewModel = hiltViewModel()
    val success by viewModel.success.collectAsState()
    val error by viewModel.error.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val photoPermissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
    var showPhotoPermissionDialog by remember { mutableStateOf(false) }

    val communityCreated = stringResource(R.string.communityCreated)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.imageUri = uri
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
    if (success) {
        LaunchedEffect(success) {
            Toast.makeText(context, communityCreated, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(integerResource(R.integer.padding).dp),
        verticalArrangement = Arrangement.spacedBy(integerResource(R.integer.mediumSpace).dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                onClick = {
                    when {
                        viewModel.name.isBlank() -> {
                            Toast.makeText(context, context.getString(R.string.nameRequired), Toast.LENGTH_SHORT).show()
                        }
                        viewModel.description.isBlank() -> {
                            Toast.makeText(context, context.getString(R.string.descriptionRequired), Toast.LENGTH_SHORT).show()
                        }
                        viewModel.category.isBlank() -> {
                            Toast.makeText(context, context.getString(R.string.categoryRequired), Toast.LENGTH_SHORT).show()
                        }
                        viewModel.imageUri == null -> {
                            Toast.makeText(context, context.getString(R.string.uploadImage), Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            viewModel.createCommunity(
                                name = viewModel.name,
                                description = viewModel.description,
                                imageUri = viewModel.imageUri,
                                category = viewModel.category,
                                currentUserId = viewModel.currentUserId
                            )
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.create))
            }
        }

        Box(
            modifier = Modifier
                .size(integerResource(R.integer.communityBoxSize).dp)
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
                                "Please allow photo access in settings",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                .background(Color.LightGray)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            if (viewModel.imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(viewModel.imageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(stringResource(R.string.tapToUploadImage), textAlign = TextAlign.Center)
            }
        }

        // Community Name input
        OutlinedTextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            label = { Text(stringResource(R.string.communityName)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Description Input
        OutlinedTextField(
            value = viewModel.description,
            onValueChange = { viewModel.description = it },
            label = { Text(stringResource(R.string.newCommDescription)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(integerResource(R.integer.descriptionBoxHeight).dp),
            maxLines = 5
        )

        // Select Community Type
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

        var selectedTypeIndex by remember { mutableStateOf(-1) }

        val selectedText = if (selectedTypeIndex >= 0) {
            communityTypes[selectedTypeIndex]
        } else {
            stringResource(R.string.select)
        }

        CommunityTypeDropdown(
            selectedText=selectedText,
            onTypeSelected = {
                selectedTypeIndex = it
                viewModel.category = communityTypes[it]
            },
            communityTypes = communityTypes
        )
    }
    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityTypeDropdown(
    selectedText: String,
    onTypeSelected: (Int) -> Unit,
    communityTypes: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        CustomTextField(
            value = selectedText,
            onValueChange = {}, // No manual input
            label = stringResource(R.string.selectCommunityType),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },

            // the number of visible items
            modifier = Modifier.heightIn(max = (4 * integerResource(R.integer.itemHeight)).dp)

        ) {
            communityTypes.forEachIndexed { index, type ->
                DropdownMenuItem(
                    text = { Text(text = type) },
                    onClick = {
                        onTypeSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}
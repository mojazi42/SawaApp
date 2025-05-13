package com.example.sawaapplication.screens.communities.presentation.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextButton
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel
import kotlinx.coroutines.delay


@Composable
fun NewCommunity(navController: NavController) {
    val context = LocalContext.current
    val viewModel: CommunityViewModel = hiltViewModel()
    val success by viewModel.success.collectAsState()
    val error by viewModel.error.collectAsState()

    val communityCreated = stringResource(R.string.communityCreated)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.imageUri = uri
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
            val uploadImage = stringResource(R.string.uploadImage)
            Button(
                onClick = {
                    if (viewModel.imageUri == null) {
                        Toast.makeText(context, uploadImage, Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.createCommunity(
                            name = viewModel.name,
                            description = viewModel.description,
                            imageUri = viewModel.imageUri,
                            currentUserId = viewModel.currentUserId
                        )
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
                .clickable { imagePickerLauncher.launch("image/*") }
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
    }
}
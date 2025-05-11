package com.example.sawaapplication.screens.event.presentation.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.event.presentation.vmModels.CreateEventViewModel
import com.example.sawaapplication.ui.screenComponent.CustomTextField
import java.text.DateFormat
import java.util.Date
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun CreateNewEventScreen(
    navController: NavHostController, communityId: String,
    viewModel: CreateEventViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val success = viewModel.success.value

    LaunchedEffect(communityId) {
        viewModel.communityId = communityId
    }
    LaunchedEffect(success) {
        if (success) {
            Toast.makeText(context, "Event Created!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            viewModel.success.value = false
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.imageUri = uri
    }
    var showDatePicker by remember { mutableStateOf(false) }
    val communityID = viewModel.communityId
    val formattedDate = viewModel.eventDate?.let {
        DateFormat.getDateInstance().format(Date(it))
    } ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(integerResource(R.integer.padding).dp),
        verticalArrangement = Arrangement.spacedBy(integerResource(R.integer.verticalArrangement).dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                    if (viewModel.membersLimit != null && viewModel.membersLimit!! > 0) {

                        if (communityID != null) {
                            viewModel.createEvent(communityID)
                        }

                        // Use membersLimit for event creation
                    } else {
                        // Show error or ignore
                    }
                },
            ) { Text(stringResource(R.string.create)) }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            stringResource(R.string.newEvent),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )

        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //event image
            Box(
                modifier = Modifier
                    .size(integerResource(R.integer.imageBoxSize).dp)
                    .clip(RoundedCornerShape(integerResource(R.integer.RoundedCornerShape).dp))
                    .clickable { imagePickerLauncher.launch("image/*") }
                    .background(Color.LightGray)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center,

                // Post image
            ) {
                if (viewModel.imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(viewModel.imageUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.AddAPhoto,
                        contentDescription = "Add photo icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            //event name
            CustomTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = stringResource(R.string.eventName),
            )

            //event description
            CustomTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = stringResource(R.string.eventDescription),
                singleLine = false,
            )

            //event location
            CustomTextField(
                value = "",
                onValueChange = {},
                label = stringResource(id = R.string.eventLocation),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.AddLocationAlt,
                        contentDescription = "set a location",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            )

            //event date
            CustomTextField(
                value = formattedDate,
                onValueChange = {},
                label = stringResource(id = R.string.eventDate),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Pick date",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                }
            )
            if (showDatePicker) {
                DatePickerModal(
                    onDateSelected = {
                        viewModel.eventDate = it
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

            //event member limit
            CustomTextField(
                value = viewModel.membersLimitInput,
                onValueChange = { viewModel.membersLimitInput = it },
                label = stringResource(R.string.eventMembersLimit),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}
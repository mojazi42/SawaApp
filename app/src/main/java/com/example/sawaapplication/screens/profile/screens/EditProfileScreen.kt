package com.example.sawaapplication.screens.profile.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sawaapplication.screens.profile.vm.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val nameState by viewModel.userName.collectAsState()
    val aboutState by viewModel.aboutMe.collectAsState()
    val profileImageUrl by viewModel.profileImageUrl.collectAsState()

    var name by remember { mutableStateOf(nameState ?: "") }
    var about by remember { mutableStateOf(aboutState ?: "") }

    // LaunchedEffect is used here to synchronize the ViewModel state (nameState, aboutState)
    // into the local editable form fields (name, about).
    // This ensures that whenever the ViewModel data updates (e.g., after Firestore loads),
    // the UI text fields are automatically populated with the latest data.
    // Without this, the fields would only initialize once and not reflect later updates.
    LaunchedEffect(nameState, aboutState) {
        name = nameState ?: ""
        about = aboutState ?: ""
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    Button(
                        onClick = {
                            if (imageUri != null) {
                                viewModel.uploadProfileImage(
                                    imageUri!!,
                                    onSuccess = {
                                        viewModel.updateName(name)
                                        viewModel.updateAboutMe(about)
                                        Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                viewModel.updateName(name)
                                viewModel.updateAboutMe(about)
                                Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B00),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Save")
                    }
                }

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Edit Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Clickable Image to Upload
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                when {
                    imageUri != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    profileImageUrl != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(profileImageUrl),
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        Text(
                            text = "Tap to upload\nimage",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = about,
                onValueChange = { about = it },
                label = { Text("About me") },
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }
    }
}

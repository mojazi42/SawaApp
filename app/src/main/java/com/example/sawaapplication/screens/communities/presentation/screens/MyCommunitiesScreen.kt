package com.example.sawaapplication.screens.communities.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sawaapplication.R
import com.example.sawaapplication.navigation.Screen
import com.example.sawaapplication.screens.communities.presentation.screens.component.MyCommunitiesCard
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel
import com.example.sawaapplication.ui.screenComponent.FloatingButton
import com.example.sawaapplication.ui.screenComponent.SearchField
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.Icon

@Composable
fun MyCommunitiesScreen(
    navController: NavController,
    viewModel: CommunityViewModel = hiltViewModel(),
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val communities by viewModel.createdCommunities.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val searchText by viewModel.searchText.collectAsState()
    val filteredCommunities by viewModel.filteredCreatedCommunities.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCreatedCommunities(currentUserId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchField(
            value = searchText,
            onValueChange = viewModel::onSearchTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = integerResource(R.integer.padding).dp)
        )
        Spacer(Modifier.height(integerResource(R.integer.mediumSpace).dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }

                filteredCommunities.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_joined_communities),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(integerResource(id = R.integer.smallerSpace).dp),
                        verticalArrangement = Arrangement.spacedBy(integerResource(id = R.integer.extraSmallSpace).dp),
                        horizontalArrangement = Arrangement.spacedBy(integerResource(id = R.integer.smallerSpace).dp),
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(filteredCommunities) { community ->
                            MyCommunitiesCard(
                                community = community,
                                onClick = {
                                    Log.d("DEBUG", "Navigating to community id: ${community.id}")
                                    navController.navigate("community_screen/${community.id}")
                                }
                            )
                        }
                    }
                    }
                }
            FloatingButton(
                onClick = { navController.navigate(Screen.CreateCommunityScreen.route) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(integerResource(R.integer.mediumSpace).dp)
            )
            }
        }
    }

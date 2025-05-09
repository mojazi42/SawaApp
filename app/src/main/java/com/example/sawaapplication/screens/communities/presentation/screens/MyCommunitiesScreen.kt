package com.example.sawaapplication.screens.communities.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sawaapplication.R
import com.example.sawaapplication.navigation.Screen
import com.example.sawaapplication.screens.communities.presentation.screens.component.MyCommunitiesCard
import com.example.sawaapplication.screens.communities.presentation.vmModels.CommunityViewModel
import com.example.sawaapplication.ui.screenComponent.FloatingButton
import com.google.firebase.auth.FirebaseAuth


@Composable
fun MyCommunitiesScreen(
    navController: NavController,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Fetch the communities when the screen is loaded
    LaunchedEffect(Unit) {
        viewModel.fetchCreatedCommunities(currentUserId)
    }

    // Observe the state of communities and loading
    val communities by viewModel.createdCommunities.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(integerResource(id= R.integer.smallerSpace).dp),
            verticalArrangement = Arrangement.spacedBy(integerResource(id= R.integer.smallerSpace).dp),
            horizontalArrangement = Arrangement.spacedBy(integerResource(id= R.integer.smallerSpace).dp),
            modifier = Modifier.padding(vertical = integerResource(id= R.integer.largerSpace).dp)
        ) {
            items(communities) { community ->
                MyCommunitiesCard(community = community)
            }
        }
        FloatingButton(
            onClick = { navController.navigate(Screen.NewCommunity.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(integerResource(R.integer.mediumSpace).dp)
            )
    }
}

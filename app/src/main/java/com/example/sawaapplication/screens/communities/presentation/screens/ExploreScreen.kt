package com.example.sawaapplication.screens.communities.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sawaapplication.screens.communities.presentation.screens.component.ExploreCommunityCardList
import com.example.sawaapplication.screens.communities.presentation.vmModels.ExploreCommunityViewModel
import com.example.sawaapplication.ui.screenComponent.SearchField

@Composable
fun ExploreScreen(
    navController: NavController,
    viewModel: ExploreCommunityViewModel = hiltViewModel()
) {
    val searchText = viewModel.searchText
    val filteredList = viewModel.filteredCommunities

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchField(
            value = searchText,
            onValueChange = viewModel::onSearchTextChange,
        )
        ExploreCommunityCardList(communities = filteredList)
    }

}



package com.example.sawaapplication.screens.communities.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.communities.presentation.screens.component.CommunityCard
import com.example.sawaapplication.screens.communities.presentation.screens.component.JoinButton
import com.example.sawaapplication.screens.communities.presentation.vmModels.ExploreCommunityViewModel

@Composable
fun ExploreScreen(
    navController: NavController,
    viewModel: ExploreCommunityViewModel = hiltViewModel()
) {

    val searchText = viewModel.searchText
    val filteredList = viewModel.filteredCommunities

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(integerResource(R.integer.profilePadding).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(integerResource(R.integer.topSpacing).dp))

        Text(
            text = stringResource(id = R.string.exploreCommunity),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = integerResource(R.integer.textSize2).sp
        )

        Spacer(Modifier.height(integerResource(R.integer.belowTitle).dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = viewModel::onSearchTextChange,
            placeholder = { Text("Search") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )

        CommunityCardList(communities = filteredList)
    }

}

@Composable
fun CommunityCardList(communities: List<String>) {

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(communities.size) { index ->
            var isJoined by remember { mutableStateOf(false) }
            CommunityCard(
                communityName = "Saudi Community",
                communityMember = 2500000,
                communityImage = painterResource(id = R.drawable.saudi_logo),
                joinButton = {
                    JoinButton(
                        isJoined = isJoined,
                        onClick = { isJoined = !isJoined }
                    )
                },
                onClick = {
                    // Handle card click
                }
            )

            if (index < 5) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }
        }
    }
}
//package com.example.sawaapplication.screens.communities.presentation.screens
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Create
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Alignment.Companion.CenterHorizontally
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.integerResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.example.sawaapplication.R
//import com.example.sawaapplication.screens.communities.presentation.vmModels.CreateCommunityViewModel
//import com.example.sawaapplication.ui.screenComponent.CustomCard
//import com.example.sawaapplication.ui.screenComponent.CustomTextField
//import com.example.sawaapplication.ui.screenComponent.GradientButton
//
//@Composable
//fun CreateCommunityScreen(
//    navController: NavController
//) {
//    val context = LocalContext.current
//    val createCommunityViewModel: CreateCommunityViewModel = hiltViewModel()
//    val authState = createCommunityViewModel.authState.collectAsState().value
//
//    var readOnly by remember { mutableStateOf(false) }
//
//    LaunchedEffect(authState) {
//        createCommunityViewModel.handleAuthStateCreateCommunity(authState, context) {
//            navController.popBackStack()
//        }
//    }
//
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = CenterHorizontally
//    ) {
//        Text(
//            text = stringResource(id = R.string.newCommunity),
//            color = Color.Black,
//            fontSize = integerResource(id = R.integer.cardHeaderSize).sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(
//                top = integerResource(id = R.integer.cardHeaderTopPadding).dp,
//                bottom = integerResource(id = R.integer.cardHeaderBottomPadding).dp
//            )
//        )
//        Spacer(modifier = Modifier.padding(bottom = integerResource(id = R.integer.extraSmallSpace).dp))
//        CustomCard(
//            modifier = Modifier
//                .size(
//                    width = integerResource(id = R.integer.cardWidth).dp,
//                    height = integerResource(id = R.integer.cardHeightSignUp).dp
//                )
//                .align(CenterHorizontally),
//        ) {
//            Spacer(modifier = Modifier.padding(bottom = integerResource(id = R.integer.extraSmallSpace).dp))
//
//            Column(
//                modifier = Modifier
//                    .padding((integerResource(id = R.integer.largeSpace).dp)),
//                horizontalAlignment = CenterHorizontally
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(integerResource(R.integer.photoBoxSize).dp)
//                ) {
//                    //Image
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_launcher_background),
//                        contentDescription = "Profile image",
//                        modifier = Modifier
//                            .clip(CircleShape),
//                    )
//
//                    //Edit icon
//                    IconButton(
//                        onClick = { readOnly = !readOnly },
//                        modifier = Modifier.align(Alignment.BottomEnd)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.Create,
//                            contentDescription = "Edit icon",
//                        )
//                    }
//                }
//                CustomTextField(
//                    value = createCommunityViewModel.name,
//                    onValueChange = { createCommunityViewModel.name = it },
//                    label = stringResource(id = R.string.communityName),
//                )
//                CustomTextField(
//                    value = createCommunityViewModel.description,
//                    onValueChange = { createCommunityViewModel.description = it },
//                    label = stringResource(id = R.string.communityDescription),
//                )
//                Spacer(modifier = Modifier.padding(integerResource(id = R.integer.smallSpace).dp))
//                GradientButton(
//                    onClick = {
//                        createCommunityViewModel.createCommunity(
//                            name = createCommunityViewModel.name,
//                            description = createCommunityViewModel.description,
//                            img = createCommunityViewModel.img
//                        )
//                    },
//                    text = stringResource(id = R.string.communityCreateBtn),
//                    modifier = Modifier.padding(top = integerResource(id = R.integer.largeSpace).dp)
//                )
//            }
//        }
//    }
//}

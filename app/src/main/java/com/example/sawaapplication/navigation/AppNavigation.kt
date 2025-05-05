package com.example.sawaapplication.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sawaapplication.core.sharedPreferences.AuthPreferences
import com.example.sawaapplication.screens.authentication.presentation.screens.LoginScreen
import com.example.sawaapplication.screens.authentication.presentation.screens.SignUpScreen
import com.example.sawaapplication.screens.onboarding.presentation.screens.OnBoardingScreen
import com.example.sawaapplication.screens.profile.screens.ProfileScreen
import kotlinx.coroutines.delay

@Composable
fun AppNavigation(
    navController: NavHostController,
) {

    var tokenState by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val authPreferences = remember { AuthPreferences(context) }

    LaunchedEffect(Unit) {
        tokenState = authPreferences.getToken()
    }

    val startDestination = if (tokenState.isNullOrEmpty()) {
        Screen.Onboarding
    } else {
        Screen.Profile // temporary, until Home is finished
    }

    Scaffold { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable<Screen.Onboarding> {
                OnBoardingScreen(navController)
            }
            composable<Screen.Login> {
                LoginScreen(navController)
            }
            composable<Screen.SignUp> {
                SignUpScreen(navController)
            }
            composable<Screen.Profile> {
                ProfileScreen(navController)
            }

        }

    }
}
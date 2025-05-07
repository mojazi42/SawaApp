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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sawaapplication.core.sharedPreferences.AuthPreferences
import com.example.sawaapplication.navigation.bottomBar.CustomBottomBar
import com.example.sawaapplication.screens.authentication.presentation.screens.LoginScreen
import com.example.sawaapplication.screens.authentication.presentation.screens.ResetPasswordScreen
import com.example.sawaapplication.screens.authentication.presentation.screens.SignUpScreen
import com.example.sawaapplication.screens.communities.presentation.CommunityScreen
import com.example.sawaapplication.screens.communities.presentation.screens.ExploreScreen
import com.example.sawaapplication.screens.home.presentation.screens.HomeScreen
import com.example.sawaapplication.screens.notification.presentation.screens.NotificationScreen
import com.example.sawaapplication.screens.communities.presentation.screens.NewCommunity
import com.example.sawaapplication.screens.onboarding.presentation.screens.OnBoardingScreen
import com.example.sawaapplication.screens.profile.screens.EditProfileScreen
import com.example.sawaapplication.screens.profile.screens.ProfileScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    //isDarkTheme: Boolean,
    //changeAppTheme: () -> Unit
) {
    var tokenState by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val authPreferences = remember { AuthPreferences(context) }

    LaunchedEffect(Unit) {
        tokenState = authPreferences.getToken()
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = bottomBarScreens.any { screen ->
        screen::class.qualifiedName == currentRoute
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                CustomBottomBar(
                    selectedIndex = bottomBarScreens.indexOfFirst { screen ->
                        screen::class.qualifiedName == currentRoute
                    },
                    onItemSelected = { selectedIndex ->
                        val selectedScreen = bottomBarScreens[selectedIndex]
                        navController.navigate(selectedScreen) {
                            popUpTo(Screen.Home) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    navController = navController
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (tokenState.isNullOrEmpty()) Screen.Onboarding else Screen.Home,
            modifier = Modifier.padding(padding)
        ) {
            composable<Screen.Onboarding> { OnBoardingScreen(navController) }
            composable<Screen.Login> { LoginScreen(navController) }
            composable<Screen.SignUp> { SignUpScreen(navController) }
            composable <Screen.ForgotPass> { ResetPasswordScreen(navController) }
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
            composable<Screen.NewCommunity> {
                NewCommunity(navController)
            }
            composable<Screen.Home> {
                HomeScreen(
                    navController = navController,
                    //isDarkTheme = isDarkTheme,
                    //changeAppTheme = changeAppTheme
                )
            }
            composable<Screen.Explore> { ExploreScreen(navController) }
            composable<Screen.Notification> { NotificationScreen(navController) }
            composable<Screen.Community> { CommunityScreen(navController) }
            composable<Screen.Profile> { ProfileScreen(navController) }

            composable("edit_profile") {
                EditProfileScreen(navController = navController)
            }
        }
    }
}


val bottomBarScreens = listOf(
    Screen.Home,
    Screen.Explore,
    Screen.Notification,
    Screen.Community,
    Screen.Profile
)
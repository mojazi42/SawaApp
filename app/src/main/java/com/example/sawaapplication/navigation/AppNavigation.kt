package com.example.sawaapplication.navigation

import android.annotation.SuppressLint
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
import com.example.sawaapplication.navigation.topBar.TopBar
import com.example.sawaapplication.screens.authentication.presentation.screens.LoginScreen
import com.example.sawaapplication.screens.authentication.presentation.screens.ResetPasswordScreen
import com.example.sawaapplication.screens.authentication.presentation.screens.SignUpScreen
import com.example.sawaapplication.screens.authentication.presentation.screens.SplashScreen
import com.example.sawaapplication.screens.communities.presentation.screens.ExploreScreen
import com.example.sawaapplication.screens.home.presentation.screens.HomeScreen
import com.example.sawaapplication.screens.notification.presentation.screens.NotificationScreen
import com.example.sawaapplication.screens.communities.presentation.screens.NewCommunity
import com.example.sawaapplication.screens.communities.presentation.screens.MyCommunitiesScreen
import com.example.sawaapplication.screens.onboarding.presentation.screens.OnBoardingScreen
import com.example.sawaapplication.screens.profile.screens.EditProfileScreen
import com.example.sawaapplication.screens.profile.screens.ProfileScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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

    val showBottomBar = bottomBarScreens.any { it.route == currentRoute }
    val showTopBar = TopBarScreens.any { screen -> screen.route == currentRoute }

    Scaffold(
        topBar = {
            if (showTopBar) {
                currentRoute?.let { route ->
                    val screen = TopBarScreens.find { it.route == route }
                    screen?.let {
                        TopBar(screen = it)
                    }
                }
            }

        },

        bottomBar = {
            if (showBottomBar) {
                CustomBottomBar(
                    selectedIndex = bottomBarScreens.indexOfFirst { screen ->
                        screen::class.qualifiedName == currentRoute
                    },
                    onItemSelected = { selectedIndex ->
                        val selectedScreen = bottomBarScreens[selectedIndex]
                        navController.navigate(selectedScreen.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    navController = navController
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (tokenState.isNullOrEmpty()) Screen.SplashScreen.route else Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.SplashScreen.route) {
                SplashScreen(navController)
            }
            composable(Screen.Onboarding.route) {
                OnBoardingScreen(navController)
            }
            composable(Screen.Login.route) {
                LoginScreen(navController)
            }
            composable(Screen.SignUp.route) {
                SignUpScreen(navController)
            }
            composable(Screen.ForgotPass.route) {
                ResetPasswordScreen(navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController)
            }
            composable(Screen.NewCommunity.route) {
                NewCommunity(navController)
            }
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }
            composable(Screen.Explore.route) {
                ExploreScreen(navController)
            }
            composable(Screen.Notification.route) {
                NotificationScreen(navController)
            }
            composable(Screen.Community.route) {
                MyCommunitiesScreen(navController)
            }
            composable(Screen.EditProfile.route) {
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

val TopBarScreens = listOf(
    Screen.Explore,
    Screen.Notification,
    Screen.Community,
    Screen.EditProfile

)
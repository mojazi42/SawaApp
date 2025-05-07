package com.example.sawaapplication.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Onboarding : Screen()

    @Serializable
    data object Login : Screen()

    @Serializable
    data object SignUp : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object Explore : Screen()

    @Serializable
    data object Community : Screen()

    @Serializable
    data object Notification : Screen()

    @Serializable
    data object ForgotPass : Screen()

    @Serializable
    data object NewCommunity : Screen()

    @Serializable
    data object SplashScreen : Screen()

}

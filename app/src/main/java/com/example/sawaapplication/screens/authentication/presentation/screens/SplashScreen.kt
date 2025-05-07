package com.example.sawaapplication.screens.authentication.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sawaapplication.R
import com.example.sawaapplication.navigation.Screen
import com.example.sawaapplication.ui.theme.OrangeText
import com.example.sawaapplication.ui.theme.SawaApplicationTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
) {
    var visible by remember { mutableStateOf(false) }

    SawaApplicationTheme(dynamicColor = false) {

        LaunchedEffect(Unit) {
            visible = true
            delay(4000)

            navController.navigate(Screen.Onboarding) {
                popUpTo(Screen.SplashScreen) { inclusive = true }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(
                        integerResource(id = R.integer.logoWidth).dp,
                        integerResource(id = R.integer.logoHeight).dp
                    )
            )
            Spacer(modifier = Modifier.height(integerResource(id = R.integer.smallSpace).dp))
            AnimatedVisibility(
                visible = visible,
                enter = slideIn(
                    animationSpec = tween(1500),
                    initialOffset = { IntOffset(0, it.height)
                    }

                ),
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = integerResource(id = R.integer.TitleFontSize).sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeText,

                    )
            }
        }
    }
}

package com.example.sawaapplication.screens.authentication.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sawaapplication.R
import com.example.sawaapplication.screens.authentication.presentation.vmModels.ForgotPasswordViewModel
import com.example.sawaapplication.screens.authentication.presentation.vmModels.LoginViewModel
import com.example.sawaapplication.ui.screenComponent.CustomCard
import com.example.sawaapplication.ui.screenComponent.CustomTextField
import com.example.sawaapplication.ui.screenComponent.GradientButton
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ResetPasswordScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val forgotPasswordViewModel: ForgotPasswordViewModel = hiltViewModel()
    val emailState by forgotPasswordViewModel.email.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.forgotPassword),
            color = Color.Black,
            fontSize = integerResource(id = R.integer.cardHeaderSize).sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                top = integerResource(id = R.integer.cardHeaderTopPadding).dp,
                bottom = integerResource(id = R.integer.cardHeaderBottomPadding).dp
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        CustomCard(
            modifier = Modifier
                .size(
                    width = integerResource(id = R.integer.cardWidthResetPass).dp,
                    height = integerResource(id = R.integer.cardHeightResetPass).dp
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                CustomTextField(
                    value = emailState,
                    onValueChange = { forgotPasswordViewModel.email.value = it },
                    label = stringResource(id = R.string.email),
                )

                Spacer(modifier = Modifier.height(integerResource(id = R.integer.cardSpacerResetPass).dp))

                GradientButton(
                    onClick = {
                        val email = emailState.trim()
                        if (email.isNotEmpty()) {
                            forgotPasswordViewModel.forgotPassword()  // Handles Firebase email sending
                            Toast.makeText(context, context.getString(R.string.SentPassword), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.failedSend),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    text = stringResource(id = R.string.resetPassword)
                )
            }
        }
    }
}

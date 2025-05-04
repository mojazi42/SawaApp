package com.example.sawaapplication.screens.authentication.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sawaapplication.R
import com.example.sawaapplication.ui.screenComponent.CustomCard
import com.example.sawaapplication.ui.screenComponent.CustomTextField
import com.example.sawaapplication.ui.screenComponent.GradientButton
import com.example.sawaapplication.ui.theme.black

@Composable
fun SignUpScreen() {
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    //need to delete when implement vm
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.Signup),
            color = Color.Black,
            fontSize = integerResource(id = R.integer.cardHeaderSize).sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                top = integerResource(id = R.integer.cardHeaderTopPadding).dp,
                bottom = integerResource(id = R.integer.cardHeaderBottomPadding).dp
            )
        )
        Spacer(modifier = Modifier.padding(bottom= integerResource(id = R.integer.extraSmallSpace).dp))
        CustomCard(
            modifier = Modifier
                .size(
                    width = integerResource(id = R.integer.cardWidth).dp,
                    height = integerResource(id = R.integer.cardHeightSignUp).dp
                )
                .align(CenterHorizontally),
        ) {
            Spacer(modifier = Modifier.padding(bottom=integerResource(id = R.integer.extraSmallSpace).dp))

            Column(
                modifier = Modifier
                    .padding((integerResource(id = R.integer.largeSpace).dp)),
                horizontalAlignment = CenterHorizontally
            ) {
                CustomTextField(
                    value = "",
                    onValueChange =  {username = it},
                    label = stringResource(id = R.string.username)
                )
                CustomTextField(
                    value ="",
                    onValueChange = {email= it},
                    label = stringResource(id = R.string.email),
                )
                CustomTextField(
                    value = "",
                    onValueChange = {password= it},
                    label = stringResource(id = R.string.password),
                    isPassword = true,
                    showPassword = showPassword,
                    onTogglePasswordVisibility = { showPassword = !showPassword },
                )
                CustomTextField(
                    value = "",
                    onValueChange ={confirmPassword = it} ,
                    label = stringResource(id = R.string.confirmPassword),
                    isPassword = true,
                    showPassword = showConfirmPassword,
                    onTogglePasswordVisibility = { showConfirmPassword = !showConfirmPassword },
                )
                Spacer(modifier = Modifier.padding(integerResource(id = R.integer.smallSpace).dp))
                GradientButton(
                    onClick = {},
                    text = stringResource(id = R.string.Signup),
                    modifier = Modifier.padding(top= integerResource(id = R.integer.largeSpace).dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = integerResource(id = R.integer.mediumSpace).dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.already_have_account),
                    fontSize = integerResource(id = R.integer.smallText).sp,
                    color = black,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(id = R.string.Login),
                    fontSize = integerResource(id = R.integer.smallText).sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { /*navController.navigate(Screen.LogIn.route)*/ }
                )
            }
        }
    }
}
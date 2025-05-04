package com.example.sawaapplication.screens.authentication.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sawaapplication.R
import com.example.sawaapplication.ui.screenComponent.CustomCard
import com.example.sawaapplication.ui.screenComponent.GradientButton

@Composable
fun SentPasswordScreen() {

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
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = CenterHorizontally
            ) {

                Text(
                    text = stringResource(id = R.string.SentPassword),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(integerResource(id = R.integer.cardSpacerResetPass).dp))

                GradientButton(
                    onClick = {},
                    text = stringResource(id = R.string.BackToLogIn)
                )
            }
        }
    }
}

package com.example.sawaapplication.ui.screenComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sawaapplication.R
import com.example.sawaapplication.ui.theme.OrangePrimary
import com.example.sawaapplication.ui.theme.firstOrange

@Composable
fun CustomButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(integerResource(id = R.integer.buttonRoundCornerShape).dp))
            .background(OrangePrimary)
/*
            .size(integerResource(id= R.integer.buttonWidth).dp, integerResource( R.integer.buttonHeight).dp),
*/
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = Color.White,
                fontSize = integerResource(id = R.integer.smallText).sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

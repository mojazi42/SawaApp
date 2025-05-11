package com.example.sawaapplication.screens.profile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.sawaapplication.ui.theme.black
import com.example.sawaapplication.ui.theme.white

@Composable
fun SettingsSwitches(
    isArabic: Boolean,
    onLanguageToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(start = 16.dp)
    ) {

        // Language Toggle (Arabic / English)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = if (isArabic) " العربية " else " English ")
            Switch(
                checked = isArabic,
                onCheckedChange = { onLanguageToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                )

            )
        }
    }
}

@Composable
fun IconSwitch(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    iconOn: ImageVector,
    iconOff: ImageVector,
    modifier: Modifier = Modifier
) {
    val thumbIcon = if (checked) iconOn else iconOff
    val backgroundColor =
        if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.9f) else Color.Gray.copy(alpha = 0.8f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable { onCheckedChange() }
            .padding(4.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Icon(
            imageVector = thumbIcon,
            contentDescription = "Switch Icon",
            tint = if (checked) black else white,
            modifier = Modifier.size(20.dp)
        )
    }
}
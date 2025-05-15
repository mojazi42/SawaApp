package com.example.sawaapplication.screens.profile.screens

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.dp
import com.example.sawaapplication.ui.theme.black
import com.example.sawaapplication.ui.theme.white
import com.example.sawaapplication.R
import java.util.Locale

@Composable
fun SettingsSwitches(
    isArabic: Boolean,
    onLanguageToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(integerResource(R.integer.mediumSpace).dp),
        modifier = Modifier.padding(start = integerResource(R.integer.padding).dp)
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
@Composable
fun SettingsThemeSwitches(
    isDark: Boolean,
    onCheckedChange: () -> Unit,

    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(integerResource(R.integer.mediumSpace).dp),
        modifier = Modifier.padding(start = integerResource(R.integer.padding).dp)
    ) {
        // Theme Toggle (Light / Dark) using IconSwitch
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = isDark,
                onCheckedChange = { onCheckedChange()},
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

fun changeAppLocale(context: Context, languageCode: String) {
    val currentLocale = context.resources.configuration.locales.get(0)
    if (currentLocale.language == languageCode) return // Prevent recreate loop

    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = context.resources.configuration
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    (context as? Activity)?.recreate()
}

package com.example.sawaapplication

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.sawaapplication.core.sharedpreferences.LanguageManager
import com.example.sawaapplication.navigation.AppNavigation
import com.example.sawaapplication.screens.profile.presentation.screens.changeAppLocale
import com.example.sawaapplication.screens.profile.presentation.vm.ThemeViewModel
import com.example.sawaapplication.ui.theme.SawaApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        // Apply saved language setting
        val savedLang = LanguageManager.getSavedLanguage(this)
        changeAppLocale(this, savedLang)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            SawaApplicationTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    navController = navController,
                    changeAppTheme = { themeViewModel.toggleTheme() },
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}
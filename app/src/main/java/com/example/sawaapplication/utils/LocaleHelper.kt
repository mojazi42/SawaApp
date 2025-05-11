package com.example.sawaapplication.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleHelper {
    fun changeLanguage(context: Context) {
        val currentLocale = context.resources.configuration.locales[0]
        val newLocale = if (currentLocale.language == "en") Locale("ar") else Locale("en")

        val config = Configuration(context.resources.configuration)
        config.setLocale(newLocale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        if (context is Activity) {
            context.recreate() // Restart the activity to apply the new locale
        }
    }
}

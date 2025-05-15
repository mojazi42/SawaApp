package com.example.sawaapplication.core.sharedPreferences

import android.content.Context

object ThemeManager {

    fun saveTheme(context: Context, isDark: Boolean){
        val sharedPreference = context.getSharedPreferences("app_prefs",Context.MODE_PRIVATE)
        sharedPreference.edit().putBoolean("theme",isDark).apply() // save theme preference
    }

    fun getSavedTheme(context: Context): Boolean{
        val sharedPreference = context.getSharedPreferences("app_prefs",Context.MODE_PRIVATE)
        return sharedPreference.getBoolean("theme",false)
    }
}
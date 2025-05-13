package com.example.sawaapplication.core.sharedPreferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

class AuthPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        sharedPrefs.edit() { putString("auth_token", token) }
    }

    fun getToken(): String? {
        return this.sharedPrefs.getString("auth_token", null)
    }

    fun clearToken() {
        sharedPrefs.edit() { remove("auth_token") }
    }
}
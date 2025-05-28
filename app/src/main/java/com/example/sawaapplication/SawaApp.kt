package com.example.sawaapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class SawaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize OneSignal
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, BuildConfig.ONESIGNAL_KEY)

        // Ask for push permission
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }
    }
}
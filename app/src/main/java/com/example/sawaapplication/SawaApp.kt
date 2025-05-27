package com.example.sawaapplication

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

        // Update player ID in FireStore if user is already signed in
        CoroutineScope(Dispatchers.IO).launch {
            val user = FirebaseAuth.getInstance().currentUser
            val playerId = OneSignal.User.pushSubscription.id

            if (user != null && !playerId.isNullOrBlank()) {
                FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(user.uid)
                    .update("oneSignalPlayerId", playerId)
            }
        }
    }
}
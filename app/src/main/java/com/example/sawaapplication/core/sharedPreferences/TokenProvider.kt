package com.example.sawaapplication.core.sharedPreferences

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TokenProvider @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    // fetch the current user's ID token
    suspend fun getToken(): String {
        val user = firebaseAuth.currentUser
        return try {
            val token = user?.getIdToken(true)?.await()?.token ?: ""
            token
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
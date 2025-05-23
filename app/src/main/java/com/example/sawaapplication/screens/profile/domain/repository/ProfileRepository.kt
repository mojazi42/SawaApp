package com.example.sawaapplication.screens.profile.domain.repository

import android.net.Uri
import com.example.sawaapplication.screens.profile.domain.model.User

interface ProfileRepository {
    suspend fun fetchAboutMe(userId: String): String?
    suspend fun fetchUserName(userId: String): String?
    suspend fun fetchProfileImageUrl(userId: String): String?
    suspend fun uploadProfileImage(uri: Uri): Boolean
    suspend fun fetchUserById(userId: String): User?
}
package com.example.sawaapplication.screens.profile.domain.repository

import android.net.Uri
import com.example.sawaapplication.screens.profile.domain.model.Badge
import com.example.sawaapplication.screens.profile.domain.model.User

interface ProfileRepository {
    suspend fun fetchAboutMe(userId: String): String?
    suspend fun fetchUserName(userId: String): String?
    suspend fun fetchProfileImageUrl(userId: String): String?
    suspend fun uploadProfileImage(uri: Uri): Boolean
    suspend fun fetchUserById(userId: String): User?
    suspend fun getAttendedEvent(userId: String): List<String>
    suspend fun grantBadges(userId: String, badgeIds: List<String>)
    suspend fun getAwardedBadges(userId: String): List<Badge>
    suspend fun getBadgeDefinitions(): List<Badge>
}
package com.example.sawaapplication.screens.profile.dataSources.repository

import android.net.Uri
import com.example.sawaapplication.screens.profile.dataSources.remote.ProfileRemoteDataSource
import com.example.sawaapplication.screens.profile.domain.model.Badge
import com.example.sawaapplication.screens.profile.domain.model.User
import com.example.sawaapplication.screens.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProfileRemoteDataSource
) : ProfileRepository {

    override suspend fun fetchAboutMe(userId: String): String? {
        return remoteDataSource.fetchAboutMe(userId)
    }

    override suspend fun fetchUserName(userId: String): String? {
        return remoteDataSource.fetchUserName(userId)
    }

    override suspend fun fetchProfileImageUrl(userId: String): String? {
        return remoteDataSource.fetchProfileImageUrl(userId)
    }

    override suspend fun uploadProfileImage(uri: Uri): Boolean {
        return remoteDataSource.uploadProfileImage(uri)
    }

    override suspend fun fetchUserById(userId: String): User? {
        return remoteDataSource.fetchUserById(userId)
    }

    override suspend fun getAttendedEvent(userId: String): List<String>{
        return remoteDataSource.getAttendedEvent(userId)
    }

    override suspend fun grantBadges(userId: String, badgeIds: List<String>){
        return remoteDataSource.grantBadges(userId,badgeIds)
    }

    override suspend fun getAwardedBadges(userId: String): List<Badge>{
        return remoteDataSource.getAwardedBadges(userId)
    }

    override suspend fun getBadgeDefinitions(): List<Badge>{
        return remoteDataSource.getBadgeDefinitions()
    }
}

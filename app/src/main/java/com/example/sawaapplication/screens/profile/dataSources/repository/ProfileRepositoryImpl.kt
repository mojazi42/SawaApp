package com.example.sawaapplication.screens.profile.dataSources.repository

import android.net.Uri
import com.example.sawaapplication.screens.profile.dataSources.remote.ProfileRemoteDataSource
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
}

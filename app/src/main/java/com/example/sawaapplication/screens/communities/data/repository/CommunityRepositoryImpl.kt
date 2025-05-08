package com.example.sawaapplication.screens.communities.data.repository

import android.net.Uri
import com.example.sawaapplication.screens.communities.data.dataSources.remote.CommunityRemoteDataSource
import javax.inject.Inject
import com.example.sawaapplication.screens.communities.domain.model.Community
import com.example.sawaapplication.screens.communities.domain.repository.CommunityRepository

class CommunityRepositoryImpl @Inject constructor(
    private val remoteDataSource: CommunityRemoteDataSource
) : CommunityRepository {


    override suspend fun createCommunity(
        community: Community, imageUri: Uri
    ): Result<Unit> {
        return remoteDataSource.createCommunity(community, imageUri)
    }


    override suspend fun fetchCommunities(userId: String): Result<List<Community>> {
        return remoteDataSource.fetchCommunity(userId)
    }

}


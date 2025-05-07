package com.example.sawaapplication.screens.communities.data.repository

import com.example.sawaapplication.screens.communities.data.dataSources.remote.CommunityRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import com.example.sawaapplication.screens.communities.domain.model.Community
import com.example.sawaapplication.screens.communities.domain.repository.CommunityRepository

class CommunityRepositoryImpl @Inject constructor(
    private val remoteDataSource: CommunityRemoteDataSource
) : CommunityRepository {

    override suspend fun createCommunity(community: Community): Result<Unit> {
        return remoteDataSource.createCommunity(community)
    }
}


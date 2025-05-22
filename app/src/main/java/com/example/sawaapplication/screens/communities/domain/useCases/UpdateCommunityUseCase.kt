package com.example.sawaapplication.screens.communities.domain.useCases

import android.net.Uri
import com.example.sawaapplication.screens.communities.data.dataSources.remote.CommunityRemoteDataSource
import javax.inject.Inject

class UpdateCommunityUseCase @Inject constructor(
    private val remoteDataSource: CommunityRemoteDataSource
){
    suspend operator fun invoke(
        communityId: String,
        name: String,
        description: String,
        category : String,
        imageUri: Uri? // nullable for optional image update
    ): Result<Unit> {
        return remoteDataSource.updateCommunity(communityId, name, description, category ,imageUri)
    }
}
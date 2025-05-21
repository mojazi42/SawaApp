package com.example.sawaapplication.screens.communities.domain.repository

import android.net.Uri
import com.example.sawaapplication.screens.communities.domain.model.Community


interface CommunityRepository {
    suspend fun createCommunity(community: Community, imageUri: Uri): Result<Unit>
    suspend fun fetchCommunities(userId: String): Result<List<Community>>
    suspend fun getCommunityById(communityId: String): Result<Community> // added function to fetch a community by its ID
    suspend fun joinCommunity(communityId: String, userId: String): Result<Unit>
    suspend fun leaveCommunity(communityId: String, userId: String): Result<Unit>
    suspend fun updateCommunity(communityId: String, name: String, description: String, imageUri: Uri?): Result<Unit>

}

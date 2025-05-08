package com.example.sawaapplication.screens.communities.domain.repository

import android.net.Uri
import com.example.sawaapplication.screens.communities.domain.model.Community


interface CommunityRepository {
    suspend fun createCommunity(community: Community, imageUri: Uri): Result<Unit>
    suspend fun fetchCommunities(userId: String): Result<List<Community>>

}

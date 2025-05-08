package com.example.sawaapplication.screens.communities.domain.useCases

import android.util.Log
import com.example.sawaapplication.screens.communities.domain.model.Community
import com.example.sawaapplication.screens.communities.domain.repository.CommunityRepository
import javax.inject.Inject

class GetUserCreatedCommunitiesUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Community>> {
        val communities = repository.fetchCommunities(userId)
        Log.d("UseCase", "Fetched communities: ${communities}")
        return communities
    }

}


package com.example.sawaapplication.screens.communities.domain.useCases

import com.example.sawaapplication.screens.communities.domain.repository.CommunityRepository
import javax.inject.Inject

class JoinCommunityUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(communityId: String, userId: String): Result<Unit> {
        return repository.joinCommunity(communityId, userId)
    }
}

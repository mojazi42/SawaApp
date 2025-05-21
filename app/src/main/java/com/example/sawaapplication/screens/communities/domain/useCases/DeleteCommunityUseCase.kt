package com.example.sawaapplication.screens.communities.domain.useCases

import com.example.sawaapplication.screens.communities.domain.repository.CommunityRepository
import javax.inject.Inject

class DeleteCommunityUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(communityId: String): Result<Unit> {
        return repository.deleteCommunity(communityId)
    }
}
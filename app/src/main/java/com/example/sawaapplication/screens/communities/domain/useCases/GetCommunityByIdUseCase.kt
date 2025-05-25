package com.example.sawaapplication.screens.communities.domain.useCases

import com.example.sawaapplication.screens.communities.domain.model.Community
import com.example.sawaapplication.screens.communities.domain.repository.CommunityRepository
import javax.inject.Inject

// Use case for retrieving a community by its ID from the repository
class GetCommunityByIdUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    // Invokes the repository to get a community and maps the result into a Community domain model
    suspend operator fun invoke(communityId: String): Result<Community> {
        return repository.getCommunityById(communityId).map { domainCommunity ->
            // Ensures the returned object is explicitly mapped (defensive mapping, may be useful if data transformation is needed later)
            Community(
                id = domainCommunity.id,
                name = domainCommunity.name,
                description = domainCommunity.description,
                image = domainCommunity.image,
                creatorId = domainCommunity.creatorId,
                category = domainCommunity.category,
                members = domainCommunity.members,
                createdAt = domainCommunity.createdAt,
                updatedAt = domainCommunity.updatedAt
            )
        }
    }
}

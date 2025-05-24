package com.example.sawaapplication.screens.communities.domain.useCases

import android.net.Uri
import com.example.sawaapplication.screens.communities.domain.model.Community
import com.example.sawaapplication.screens.communities.domain.repository.CommunityRepository
import javax.inject.Inject

class CreateCommunityUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(
        name: String, description: String, category: String, imageUri: Uri?, creatorId: String
    ): Result<Unit> {
        if (imageUri == null) return Result.failure(Exception("Image URI is null"))

        val community = Community(
            name = name,
            description = description,
            image = "",
            category = category,
            creatorId = creatorId,
            members = listOf(creatorId),
            createdAt = System.currentTimeMillis().toString(),
            updatedAt = System.currentTimeMillis().toString()
        )

        return repository.createCommunity(community, imageUri)
    }

}
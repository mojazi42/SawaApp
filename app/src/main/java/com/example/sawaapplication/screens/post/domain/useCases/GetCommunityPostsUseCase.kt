package com.example.sawaapplication.screens.post.domain.useCases

import com.example.sawaapplication.screens.post.domain.model.Post
import com.example.sawaapplication.screens.post.domain.repository.PostRepository
import javax.inject.Inject

class GetCommunityPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(communityId: String): Result<List<Post>> {
        return repository.getPostsForCommunity(communityId)
    }
}

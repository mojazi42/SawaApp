package com.example.sawaapplication.screens.home.domain.useCases

import com.example.sawaapplication.screens.home.domain.repository.HomeRepository
import com.example.sawaapplication.screens.post.domain.model.Post
import javax.inject.Inject


class LikePostUseCase @Inject constructor(
    private val repository: HomeRepository
)  {
    suspend operator fun invoke(post: Post, postDocId: String?): Pair<Post?, String?>{
        return repository.likePost(post,postDocId)
    }
}
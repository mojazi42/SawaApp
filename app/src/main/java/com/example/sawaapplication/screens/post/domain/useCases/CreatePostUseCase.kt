package com.example.sawaapplication.screens.post.domain.useCases

import android.net.Uri
import com.example.sawaapplication.screens.post.domain.model.Post
import com.example.sawaapplication.screens.post.domain.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {

    suspend operator fun invoke(communityId: String, post: Post, imageUri: Uri?) {
        repository.createPostInCommunity(communityId, post, imageUri)
    }
}
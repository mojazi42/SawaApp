package com.example.sawaapplication.screens.post.domain.repository

import android.net.Uri
import com.example.sawaapplication.screens.post.domain.model.Post
import com.example.sawaapplication.screens.post.domain.model.PostUiModel

interface PostRepository {
    suspend fun createPostInCommunity(communityId: String, post: Post, imageUri: Uri?)
    suspend fun getPostsForCommunity(communityId: String): Result<List<PostUiModel>>
}

package com.example.sawaapplication.screens.post.data.repository

import android.net.Uri
import com.example.sawaapplication.screens.post.data.dataSources.remote.PostsInCommunityRemote
import com.example.sawaapplication.screens.post.domain.model.Post
import com.example.sawaapplication.screens.post.domain.model.PostUiModel
import com.example.sawaapplication.screens.post.domain.repository.PostRepository
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val remoteDataSource: PostsInCommunityRemote
) : PostRepository {
    override suspend fun createPostInCommunity(communityId: String, post: Post, imageUri: Uri?) {
        remoteDataSource.createPostInCommunity(communityId, post, imageUri)
    }
    override suspend fun getPostsForCommunity(communityId: String): Result<List<PostUiModel>> {
        return remoteDataSource.getPostsForCommunity(communityId)
    }

}

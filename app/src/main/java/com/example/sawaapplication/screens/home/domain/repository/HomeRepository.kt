package com.example.sawaapplication.screens.home.domain.repository

import android.net.Uri
import com.example.sawaapplication.screens.event.domain.model.Event
import com.example.sawaapplication.screens.post.domain.model.Post
import com.example.sawaapplication.screens.profile.domain.model.User

interface HomeRepository {
    suspend fun getUserCommunityIds(userId: String):List<String>
    suspend fun fetchAllPosts(): Pair<List<Post>, Map<Post, String>>
    suspend fun fetchCommunityNames(id: List<String>):Map<String, String>
    suspend fun fetchUserDetails(userId: List<String>): Map<String, Pair<String, String>>
    suspend fun likePost(post: Post, postDocId: String?): Pair<Post?, String?>
    suspend fun fetchPostsByUser(userId: String): Pair<List<Post>, Map<Post, String>>
    suspend fun fetchLikedPostsByUser(userId: String): Pair<List<Post>, Map<Post, String>>
    suspend fun deletePost(post: Post, docId: String)
    suspend fun fetchJoinedEvents(userId: String): List<Event>
}
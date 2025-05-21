package com.example.sawaapplication.screens.post.domain.model

import java.util.Date

data class PostUiModel(
    val id: String,
    val username: String,
    val userAvatarUrl: String = "",
    val postImageUrl: String = "",
    val content: String = "",
    val likes: Int = 0,
    val likedBy: List<String> = emptyList(),
    val userId: String = "",
    val createdAt: Date = Date(),
    val communityId: String = ""
)



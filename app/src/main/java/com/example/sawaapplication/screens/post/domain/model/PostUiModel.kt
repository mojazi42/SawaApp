package com.example.sawaapplication.screens.post.domain.model

data class PostUiModel(
    val username: String,
    val userAvatarUrl: String = "",
    val postImageUrl: String = "",
    val content: String = ""
)

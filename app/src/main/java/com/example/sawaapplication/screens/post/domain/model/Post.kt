package com.example.sawaapplication.screens.post.domain.model

data class Post(
    val userId: String = "", // Reference to User collection
    val content: String = "" ,
    val imageUri: String = "" ,
    val likes: Int = 0 ,
    val createdAt: String = "",
    val communityId: String = ""
)

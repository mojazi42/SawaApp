package com.example.sawaapplication.screens.post.domain.model

data class Post(
    val id : String = "",
    val username: String = "",
    val userIcon: String = "",
    val content: String = "" ,
    val imageUri: String = "" ,
    val likes: Int = 0 ,
    val createdAt: String = "",
    val communityId: String = ""
)

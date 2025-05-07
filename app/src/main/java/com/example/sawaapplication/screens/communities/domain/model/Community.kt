package com.example.sawaapplication.screens.communities.domain.model

data class Community(
    val name: String,
    val description: String,
    val creatorId: String,
    val image: String,
    val members: List<String>,
    val createdAt: String,
    val updatedAt: String
)

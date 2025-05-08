package com.example.sawaapplication.screens.communities.domain.model

data class Community(
    val name: String = "",
    val description: String = "",
    val image: String = "",
    val creatorId: String = "",
    val members: List<String> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)



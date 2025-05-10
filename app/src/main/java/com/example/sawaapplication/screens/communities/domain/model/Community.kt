package com.example.sawaapplication.screens.communities.domain.model

data class Community(
    val id: String = "", // id field to hold Firestore document ID
    val name: String = "",
    val description: String = "",
    val image: String = "",
    val creatorId: String = "",
    val members: List<String> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)



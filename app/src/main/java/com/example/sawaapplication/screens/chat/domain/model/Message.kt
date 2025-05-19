package com.example.sawaapplication.screens.chat.domain.model

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val createdAt: Timestamp? = null,
    val readBy: Map<String, Boolean> = emptyMap(),
    val imageUrl: String = ""
)
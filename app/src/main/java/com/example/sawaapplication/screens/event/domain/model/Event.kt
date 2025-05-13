package com.example.sawaapplication.screens.event.domain.model

import com.google.firebase.firestore.GeoPoint

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val memberLimit: Int = 0,
    val createdBy: String ="",
    val imageUri:String = "",
    val joinedUsers: List<String> = emptyList()
)


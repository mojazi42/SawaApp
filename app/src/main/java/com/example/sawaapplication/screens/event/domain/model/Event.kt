package com.example.sawaapplication.screens.event.domain.model

import com.google.firebase.firestore.GeoPoint

data class Event(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val location: GeoPoint,
    val memberLimit: Int = 0,
    val createdBy: String ="",
    val imageUri:String = ""
)

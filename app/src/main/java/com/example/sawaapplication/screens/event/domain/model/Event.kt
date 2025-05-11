package com.example.sawaapplication.screens.event.domain.model

data class Event(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val location: String = "",
    val memberLimit: Int = 0,
    val createdBy: String ="",
    val imageUri:String = ""
)

package com.example.sawaapplication.screens.notification.domain.model

import com.google.firebase.Timestamp
import java.util.Date

data class Notification(
    val id: String,
    val message: String,
    val timestamp: Date,
    val userId: String,
    val isRead: Boolean,
    val type: String = "",
    val eventId: String? = null,
    val startTime: Timestamp? = null,
    val responded: Boolean = false,
)

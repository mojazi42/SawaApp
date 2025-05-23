package com.example.sawaapplication.screens.event.presentation.screens

import com.example.sawaapplication.screens.event.domain.model.Event

fun Event.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    title.takeIf { it.isNotBlank() }?.let { map["title"] = it }
    description.takeIf { it.isNotBlank() }?.let { map["description"] = it }
    location?.let { map["location"] = it }
    time?.let { map["time"] = it }
    date.takeIf { it.isNotBlank() }?.let { map["date"] = it }
    if (memberLimit > 0) map["memberLimit"] = memberLimit
    if (!imageUri.isNullOrBlank()) map["imageUri"] = imageUri
    map["latitude"] = latitude
    map["longitude"] = longitude
    return map
}


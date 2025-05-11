package com.example.sawaapplication.screens.event.presentation.vmModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawaapplication.screens.event.domain.model.Event
import com.example.sawaapplication.screens.event.domain.useCases.CreateEventUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase,
) : ViewModel() {

    var communityId by mutableStateOf<String?>("")
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var eventDate by mutableStateOf<Long?>(System.currentTimeMillis())
    var imageUri by mutableStateOf<Uri?>(null)
    var membersLimitInput by mutableStateOf("")

    val membersLimit: Int?
        get() = membersLimitInput.toIntOrNull()

    val uid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    fun createEvent(communityId: String) {
        if (communityId == null) {
            Log.e("CreateEvent", "Missing communityId. Cannot create event.")
            return
        }

        val eventName = name.trim()
        val eventDesc = description.trim()
        val eventDateValue = eventDate ?: System.currentTimeMillis()
        val limit = membersLimit ?: 0
        val image = imageUri?.toString().orEmpty()

        val event = Event(
            title = eventName,
            location = "",
            date = Date(eventDateValue).toString(),
            description = eventDesc,
            memberLimit = limit,
            createdBy = uid,
            imageUri = image
        )

        viewModelScope.launch {
            try {
                Log.d("CreateEvent", "Creating event in communityId: $communityId")
                createEventUseCase(communityId!!, event, imageUri!!)
                Log.d("CreateEvent", "Event created successfully")
            } catch (e: Exception) {
                Log.e("CreateEvent", "Error creating event: ${e.message}", e)
            }
        }
    }


}

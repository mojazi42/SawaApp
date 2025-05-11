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
import kotlinx.coroutines.Job

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

    private var job: Job? = null
    val loading = mutableStateOf(false)
    val success = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)


    val membersLimit: Int?
        get() = membersLimitInput.toIntOrNull()

    val uid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    fun createEvent(communityId: String) {
        if (loading.value) return
        if (imageUri == null) {
            error.value = "Please select an image."

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

        job = viewModelScope.launch {
            loading.value = true
            try {
                createEventUseCase(communityId, event, imageUri!!)
                success.value = true
                Log.d("CreateEvent", "Event created successfully ${communityId}")
            } catch (e: Exception) {
                error.value = "Failed to create event: ${e.message}"
                Log.e("CreateEvent", "Error creating event", e)
            } finally {
                loading.value = false
            }
        }
    }
}

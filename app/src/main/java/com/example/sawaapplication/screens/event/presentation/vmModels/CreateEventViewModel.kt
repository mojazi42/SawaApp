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
import com.example.sawaapplication.core.permissions.PermissionHandler
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Job
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase,
    private val permissionHandler: PermissionHandler
) : ViewModel() {

    var communityId by mutableStateOf<String?>("")
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var eventDate by mutableStateOf<Long?>(System.currentTimeMillis())
    var imageUri by mutableStateOf<Uri?>(null)
    var membersLimitInput by mutableStateOf("")
    var eventTime by mutableStateOf("")
    var location by mutableStateOf(GeoPoint(0.0, 0.0))
    var locationText by mutableStateOf("Location not set")
    private var job: Job? = null
    val loading = mutableStateOf(false)
    val success = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    var isMapVisible by mutableStateOf(false)


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


        val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        val parsedTime = timeFormat.parse(eventTime)

        val calendar = Calendar.getInstance()

        calendar.timeInMillis = eventDate ?: System.currentTimeMillis()

        parsedTime?.let {
            val timeCal = Calendar.getInstance()
            timeCal.time = it

            calendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }

        val finalTimestamp = Timestamp(calendar.time)

        val event = Event(
            title = name.trim(),
            location = location,
            date = Date(eventDate ?: System.currentTimeMillis()).toString(), // optional
            time = finalTimestamp,
            description = description.trim(),
            memberLimit = membersLimit ?: 0,
            createdBy = uid,
            imageUri = imageUri?.toString().orEmpty(),
            latitude = location.latitude,
            longitude = location.longitude
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

    fun shouldRequestLocation() = permissionHandler.shouldRequestLocationPermission()
    fun markLocationPermissionRequested() = permissionHandler.markLocationPermissionRequested()

    fun shouldRequestPhoto() = permissionHandler.shouldRequestPhotoPermission()
    fun markPhotoPermissionRequested() = permissionHandler.markPhotoPermissionRequested()
}

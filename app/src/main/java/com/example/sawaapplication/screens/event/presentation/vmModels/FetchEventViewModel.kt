package com.example.sawaapplication.screens.event.presentation.vmModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawaapplication.screens.event.domain.model.Event
import com.example.sawaapplication.screens.event.domain.repository.EventRepository
import com.example.sawaapplication.screens.event.domain.useCases.GetAllEventInCommunity
import com.example.sawaapplication.screens.event.domain.useCases.JoinEventUseCase
import com.example.sawaapplication.screens.event.domain.useCases.LeaveEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FetchEventViewModel @Inject constructor(
    private val getAllEventInCommunity: GetAllEventInCommunity,
    private val joinEventUseCase: JoinEventUseCase,
    private val leaveEventUseCase: LeaveEventUseCase,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _joinResult = MutableStateFlow<Result<Unit>?>(null)
    val joinResult: StateFlow<Result<Unit>?> = _joinResult

    fun loadEvents(communityId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = getAllEventInCommunity(communityId)
            result
                .onSuccess { events ->
                    Log.d("EVENT_VIEWMODEL", "Fetched ${events.size} events")
                    _events.value = events
                }
                .onFailure { e ->
                    Log.e("EVENT_VIEWMODEL", "Failed to load events: ${e.message}", e)
                    _error.value = e.message
                }

            _loading.value = false
        }
    }

    fun joinEvent(communityId: String, eventId: String, userId: String) {
        viewModelScope.launch {
            _joinResult.value = null
            try {
                val result = joinEventUseCase(communityId, eventId, userId)
                _joinResult.value = result

                if (result.isSuccess) {
                    // Refresh event list to update joinedUsers
                    loadEvents(communityId)
                }
            } catch (e: Exception) {
                _joinResult.value = Result.failure(e)
            }
        }
    }

    fun leaveEvent(communityId: String, eventId: String, userId: String) {
        viewModelScope.launch {
            _joinResult.value = null
            try {
                val result = leaveEventUseCase(communityId, eventId, userId)
                _joinResult.value = result

                if (result.isSuccess) {
                    loadEvents(communityId)
                }
            } catch (e: Exception) {
                _joinResult.value = Result.failure(e)
            }
        }
    }

    fun deleteEvent(communityId: String, eventId: String) {
        viewModelScope.launch {
            val result = eventRepository.deleteEvent(communityId, eventId)
            if (result.isSuccess) {
                _events.value = _events.value.filterNot { it.id == eventId }
            } else {
                Log.e("DeleteEvent", "Error: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun updateEvent(communityId: String, eventId: String, updatedData: Map<String, Any>) {
        viewModelScope.launch {
            val result = eventRepository.updateEvent(communityId, eventId, updatedData)
            if (result.isSuccess) {
                loadEvents(communityId)
            } else {
                Log.e("UpdateEvent", "Error: ${result.exceptionOrNull()?.message}")
            }
        }
    }




}
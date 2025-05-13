package com.example.sawaapplication.screens.event.presentation.vmModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawaapplication.screens.event.domain.model.Event
import com.example.sawaapplication.screens.event.domain.useCases.GetAllEventInCommunity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FetchEventViewModel @Inject constructor(
    private val getAllEventInCommunity: GetAllEventInCommunity
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

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

}

package com.example.sawaapplication.screens.communities.presentation.vmModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.sawaapplication.screens.communities.domain.useCases.CreateCommunityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.sawaapplication.screens.communities.domain.model.Community
import com.example.sawaapplication.screens.communities.domain.useCases.GetUserCreatedCommunitiesUseCase

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val createCommunityUseCase: CreateCommunityUseCase,
    private val getUserCreatedCommunitiesUseCase: GetUserCreatedCommunitiesUseCase
) : ViewModel() {

    private val _createdCommunities = MutableStateFlow<List<Community>>(emptyList())
    val createdCommunities: StateFlow<List<Community>> = _createdCommunities

    // Add a StateFlow for loading/error handling
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Create community function with feedback on success/failure
    fun createCommunity(
        name: String,
        description: String,
        imageUri: Uri?,
        currentUserId: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                // Upload image if necessary and create community
                // Assuming createCommunityUseCase takes care of adding image URI to Firestore
                createCommunityUseCase(name, description, imageUri, currentUserId)
                fetchCreatedCommunities(currentUserId)  // Refresh the communities
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Failed to create community: ${e.message}"
            }
        }
    }

    // Fetch communities after creation
    fun fetchCreatedCommunities(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = getUserCreatedCommunitiesUseCase(userId)
                result.onSuccess { communities ->
                    Log.d("CommunityViewModel", "Fetched $communities communities")
                    _createdCommunities.value = communities
                    _error.value = null  // Clear previous errors if any
                }.onFailure { exception ->
                    Log.e("CommunityViewModel", "Error fetching communities", exception)
                    _error.value = "Failed to fetch communities: ${exception.message}"
                }
            } catch (e: Exception) {
                Log.e("CommunityViewModel", "Unexpected error fetching communities", e)
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

}




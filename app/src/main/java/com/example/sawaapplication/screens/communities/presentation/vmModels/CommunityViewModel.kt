package com.example.sawaapplication.screens.communities.presentation.vmModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.Job
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth


@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val createCommunityUseCase: CreateCommunityUseCase,
    private val getUserCreatedCommunitiesUseCase: GetUserCreatedCommunitiesUseCase,
    firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private var job: Job? = null
    var imageUri by mutableStateOf<Uri?>(null)
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    val currentUserId = firebaseAuth.currentUser?.uid ?: ""
    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success
    private val _createdCommunities = MutableStateFlow<List<Community>>(emptyList())
    val createdCommunities: StateFlow<List<Community>> = _createdCommunities
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createCommunity(
        name: String,
        description: String,
        imageUri: Uri?,
        currentUserId: String
    ) {
        if (_loading.value) return
        job = viewModelScope.launch {
            _loading.value = true
            try {
                val result = createCommunityUseCase(name, description, imageUri, currentUserId)

                result.onSuccess {
                    fetchCreatedCommunities(currentUserId)
                    _success.value = true
                    _loading.value = false
                }.onFailure {
                    _error.value = "Failed to create community: ${it.message}"
                    _loading.value = false
                }
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Unexpected error: ${e.message}"
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
                    _createdCommunities.value = communities
                    _error.value = null  // Clear previous errors if any
                }.onFailure { exception ->
                    _error.value = "Failed to fetch communities: ${exception.message}"
                }
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}





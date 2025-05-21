package com.example.sawaapplication.screens.communities.presentation.vmModels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawaapplication.core.permissions.PermissionHandler
import com.example.sawaapplication.screens.communities.domain.model.Community
import com.example.sawaapplication.screens.communities.domain.useCases.*
import com.example.sawaapplication.screens.post.domain.model.PostUiModel
import com.example.sawaapplication.screens.post.domain.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val createCommunityUseCase: CreateCommunityUseCase,
    private val getUserCreatedCommunitiesUseCase: GetUserCreatedCommunitiesUseCase,
    private val getCommunityByIdUseCase: GetCommunityByIdUseCase,
    private val updateCommunityUseCase : UpdateCommunityUseCase,
    private val postRepository: PostRepository,
    private val permissionHandler: PermissionHandler,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private var job: Job? = null

    // UI-bound form fields
    var imageUri by mutableStateOf<Uri?>(null)
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var category by mutableStateOf("")

    // Current user ID from Firebase
    val currentUserId = firebaseAuth.currentUser?.uid ?: ""

    // Holds the details of a single community for the detail screen
    private val _communityDetail = MutableStateFlow<Community?>(null)
    val communityDetail: StateFlow<Community?> = _communityDetail

    // Check if the current user is also the admin of community
    val isAdmin: StateFlow<Boolean> = communityDetail.map { it?.creatorId == currentUserId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Tracks whether the community creation was successful
    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    // Stores the list of communities created by the current user
    private val _createdCommunities = MutableStateFlow<List<Community>>(emptyList())
    val createdCommunities: StateFlow<List<Community>> = _createdCommunities

    // Loading and error states for UI feedback
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    //Fetching the posts in side community
    private val _communityPosts = MutableStateFlow<List<PostUiModel>>(emptyList())
    val communityPosts: StateFlow<List<PostUiModel>> = _communityPosts

    // Holds the current search text
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    var selectedFilter by mutableStateOf<CommunityFilterType>(CommunityFilterType.DEFAULT)

    fun onSearchTextChange(newText: String) {
        _searchText.value = newText
    }


    fun shouldRequestPhoto() = permissionHandler.shouldRequestPhotoPermission()
    fun markPhotoPermissionRequested() = permissionHandler.markPhotoPermissionRequested()



    // Creates a new community, uploads its image, and updates state
    fun createCommunity(
        name: String,
        description: String,
        imageUri: Uri?,
        category: String,
        currentUserId: String
    ) {
        if (_loading.value) return
        job = viewModelScope.launch {
            _loading.value = true
            try {
                val result = createCommunityUseCase(name=name, description=description, category=category, imageUri=imageUri, currentUserId)
                result.onSuccess {
                    fetchCreatedCommunities(currentUserId) // Refresh list after successful creation
                    _success.value = true
                }.onFailure {
                    _error.value = "Failed to create community: ${it.message}"
                }
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Filtered list based on search text
    val filteredCreatedCommunities: StateFlow<List<Community>> =
        combine(_searchText, _createdCommunities) { query, communities ->

            var filtered = if (query.isBlank()) {
                communities
            } else {
                communities.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }

            when (val filter = selectedFilter) {
                is CommunityFilterType.DEFAULT -> {
                    filtered = filtered.sortedBy { it.createdAt.toLongOrNull() ?: Long.MAX_VALUE }
                }

                is CommunityFilterType.MOST_POPULAR -> {
                    filtered = filtered.sortedByDescending { it.members.size }
                }

                is CommunityFilterType.MOST_RECENT -> {
                    filtered = filtered.sortedByDescending { it.createdAt.toLongOrNull() ?: 0L }
                }

                is CommunityFilterType.Category -> {
                    filtered = filtered.filter {
                        it.category.equals(filter.categoryName, ignoreCase = true)
                    }
                }
            }

            filtered
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun shouldRequestLocation() = permissionHandler.shouldRequestLocationPermission()
    fun markLocationPermissionRequested() = permissionHandler.markLocationPermissionRequested()

    fun shouldRequestPhoto() = permissionHandler.shouldRequestPhotoPermission()
    fun markPhotoPermissionRequested() = permissionHandler.markPhotoPermissionRequested()


    // Fetches communities where the current user is a member
    fun fetchCreatedCommunities(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = getUserCreatedCommunitiesUseCase(userId)
                result.onSuccess {
                    _createdCommunities.value = it
                    _error.value = null
                }.onFailure {
                    _error.value = "Failed to fetch communities: ${it.message}"
                }
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Fetches the full detail of a single community by its ID
    fun fetchCommunityDetail(communityId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = getCommunityByIdUseCase(communityId)
            result.onSuccess {
                _communityDetail.value = it
            }.onFailure {
                _error.value = "Failed to load community detail: ${it.message}"
            }
            _loading.value = false
        }
    }

    fun fetchPostsForCommunity(communityId: String) {
        viewModelScope.launch {
            val result = postRepository.getPostsForCommunity(communityId)
            result.onSuccess { posts ->
                _communityPosts.value = posts
                    .sortedBy { it.createdAt }
                    .map {
                    PostUiModel(
                        id = it.id,
                        username = it.username,
                        userAvatarUrl = it.userAvatarUrl,
                        postImageUrl = it.postImageUrl,
                        content = it.content,
                        likes = it.likes,
                        likedBy = it.likedBy,
                        userId = it.userId,
                        communityId = it.communityId
                    )
                }
            }.onFailure {
                Log.e("CommunityViewModel", "Failed to fetch posts: ${it.message}")
            }
        }
    }

    fun likePost(post: PostUiModel) {
        viewModelScope.launch {
            try {
                val currentUserId = firebaseAuth.currentUser?.uid ?: return@launch
                val postRef = firestore.collection("Community")
                    .document(post.communityId)
                    .collection("posts")
                    .document(post.id)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(postRef)
                    val likedBy = snapshot.get("likedBy") as? List<String> ?: emptyList()
                    val isLiked = currentUserId in likedBy
                    val newLikedBy = if (isLiked) likedBy - currentUserId else likedBy + currentUserId
                    val newLikes = newLikedBy.size

                    transaction.update(postRef, mapOf(
                        "likes" to newLikes,
                        "likedBy" to newLikedBy
                    ))
                }.await()

                // Locally update the UI
                _communityPosts.update { posts ->
                    posts.map {
                        if (it.id == post.id) {
                            val isCurrentlyLiked = currentUserId in it.likedBy
                            it.copy(
                                likes = if (isCurrentlyLiked) it.likes - 1 else it.likes + 1,
                                likedBy = if (isCurrentlyLiked)
                                    it.likedBy - currentUserId
                                else
                                    it.likedBy + currentUserId
                            )
                        } else it
                    }
                }

            } catch (e: Exception) {
                Log.e("CommunityViewModel", "Failed to like post: ${e.message}")
            }
        }
    }

    fun updateCommunity(
        communityId: String,
        name: String,
        description: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = updateCommunityUseCase(communityId, name, description, imageUri)
                result.onSuccess {
                    _success.value = true
                    fetchCommunityDetail(communityId) // Refresh updated data
                }.onFailure {
                    _error.value = "Update failed: ${it.message}"
                }
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }
}

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val createCommunityUseCase: CreateCommunityUseCase,
    private val getUserCreatedCommunitiesUseCase: GetUserCreatedCommunitiesUseCase,
    private val getCommunityByIdUseCase: GetCommunityByIdUseCase,
    private val postRepository: PostRepository,
    private val permissionHandler: PermissionHandler,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private var job: Job? = null
    val currentUserId = firebaseAuth.currentUser?.uid.orEmpty()

    var imageUri by mutableStateOf<Uri?>(null)
    var name by mutableStateOf("")
    var description by mutableStateOf("")

    private val _communityDetail = MutableStateFlow<Community?>(null)
    val communityDetail: StateFlow<Community?> = _communityDetail

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _createdCommunities = MutableStateFlow<List<Community>>(emptyList())
    val createdCommunities: StateFlow<List<Community>> = _createdCommunities

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _communityPosts = MutableStateFlow<List<PostUiModel>>(emptyList())
    val communityPosts: StateFlow<List<PostUiModel>> = _communityPosts

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    val filteredCreatedCommunities = combine(_searchText, _createdCommunities) { query, list ->
        if (query.isBlank()) list else list.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchTextChange(newText: String) {
        _searchText.value = newText
    }

    fun shouldRequestLocation() = permissionHandler.shouldRequestLocationPermission()
    fun markLocationPermissionRequested() = permissionHandler.markLocationPermissionRequested()
    fun shouldRequestPhoto() = permissionHandler.shouldRequestPhotoPermission()
    fun markPhotoPermissionRequested() = permissionHandler.markPhotoPermissionRequested()

    fun createCommunity(name: String, description: String, imageUri: Uri?, currentUserId: String) {
        if (_loading.value) return
        job = viewModelScope.launch {
            _loading.value = true
            try {
                val result = createCommunityUseCase(name, description, imageUri, currentUserId)
                result.onSuccess {
                    fetchCreatedCommunities(currentUserId)
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
                _communityPosts.value = posts.map {
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

                // ✅ Locally update the UI
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

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }
}

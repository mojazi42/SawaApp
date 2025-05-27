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
    private val updateCommunityUseCase : UpdateCommunityUseCase,
    private val deleteCommunityUseCase : DeleteCommunityUseCase,
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

    // Track current user's membership status
    private val _isMember = MutableStateFlow(false)
    val isMember: StateFlow<Boolean> = _isMember

    // Track if user can join events (must be a member)
    val canJoinEvents: StateFlow<Boolean> = _isMember
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Track if user can interact with posts (must be a member)
    val canInteractWithPosts: StateFlow<Boolean> = _isMember
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Tracks whether the community creation was successful
    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Stores the list of communities created by the current user
    private val _createdCommunities = MutableStateFlow<List<Community>>(emptyList())
    val createdCommunities: StateFlow<List<Community>> = _createdCommunities

    // Loading and error states for UI feedback
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading


    //Fetching the posts in side community
    private val _communityPosts = MutableStateFlow<List<PostUiModel>>(emptyList())
    val communityPosts: StateFlow<List<PostUiModel>> = _communityPosts

    // Holds the current search text
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    var selectedFilter by mutableStateOf<CommunityFilterType>(CommunityFilterType.DEFAULT)

    private val _actionType = MutableStateFlow<String?>(null)
    val actionType: StateFlow<String?> = _actionType

    fun onSearchTextChange(newText: String) {
        _searchText.value = newText
    }

    fun shouldRequestPhoto() = permissionHandler.shouldRequestPhotoPermission()
    fun markPhotoPermissionRequested() = permissionHandler.markPhotoPermissionRequested()

    /**
     * Check if current user is a member of the community
     */
    fun checkMembershipStatus(communityId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = firebaseAuth.currentUser?.uid ?: return@launch
                val communityRef = firestore.collection("Community").document(communityId)
                val snapshot = communityRef.get().await()

                val members = snapshot.get("members") as? List<String> ?: emptyList()
                _isMember.value = currentUserId in members

            } catch (e: Exception) {
                Log.e("CommunityViewModel", "Failed to check membership: ${e.message}")
                _isMember.value = false
            }
        }
    }

    /**
     * Join a community
     */
    fun joinCommunity(communityId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val currentUserId = firebaseAuth.currentUser?.uid ?: return@launch
                val communityRef = firestore.collection("Community").document(communityId)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(communityRef)
                    val members = snapshot.get("members") as? MutableList<String> ?: mutableListOf()

                    if (currentUserId !in members) {
                        members.add(currentUserId)
                        transaction.update(communityRef, "members", members)
                    }
                }.await()

                _isMember.value = true
                // Refresh community detail to show updated member count
                fetchCommunityDetail(communityId)

            } catch (e: Exception) {
                _error.value = "Failed to join community: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Leave a community
     */
    fun leaveCommunity(communityId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val currentUserId = firebaseAuth.currentUser?.uid ?: return@launch
                val communityRef = firestore.collection("Community").document(communityId)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(communityRef)
                    val members = snapshot.get("members") as? MutableList<String> ?: mutableListOf()

                    if (currentUserId in members) {
                        members.remove(currentUserId)
                        transaction.update(communityRef, "members", members)
                    }
                }.await()

                _isMember.value = false
                // Refresh community detail to show updated member count
                fetchCommunityDetail(communityId)

            } catch (e: Exception) {
                _error.value = "Failed to leave community: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Check if user can join an event (must be community member)
     */
    fun canUserJoinEvent(communityId: String): Boolean {
        return _isMember.value
    }

    /**
     * Enhanced event joining with strict community membership validation
     */
    fun joinEventWithMembershipCheck(
        communityId: String,
        eventId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val currentUserId = firebaseAuth.currentUser?.uid ?: run {
                    onFailure("User not authenticated")
                    return@launch
                }

                // First, verify community membership from Firestore (server-side validation)
                val communityRef = firestore.collection("Community").document(communityId)
                val communitySnapshot = communityRef.get().await()
                val members = communitySnapshot.get("members") as? List<String> ?: emptyList()

                if (currentUserId !in members) {
                    onFailure("You must join the community first to participate in events")
                    return@launch
                }

                // If user is a member, proceed with event joining
                val eventRef = firestore.collection("Community")
                    .document(communityId)
                    .collection("events")
                    .document(eventId)

                firestore.runTransaction { transaction ->
                    val eventSnapshot = transaction.get(eventRef)
                    val joinedUsers = eventSnapshot.get("joinedUsers") as? MutableList<String> ?: mutableListOf()
                    val memberLimit = eventSnapshot.getLong("memberLimit")?.toInt() ?: Int.MAX_VALUE

                    // Check if event is full
                    if (joinedUsers.size >= memberLimit) {
                        throw Exception("Event is full")
                    }

                    // Check if user is already joined
                    if (currentUserId in joinedUsers) {
                        throw Exception("You are already registered for this event")
                    }

                    joinedUsers.add(currentUserId)
                    transaction.update(eventRef, "joinedUsers", joinedUsers)
                }.await()

                onSuccess()

            } catch (e: Exception) {
                onFailure("Failed to join event: ${e.message}")
            }
        }
    }

    /**
     * Check if user can join a specific event with server-side validation
     */
    fun canUserJoinEventAsync(communityId: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val currentUserId = firebaseAuth.currentUser?.uid ?: run {
                    callback(false, "User not authenticated")
                    return@launch
                }

                val communityRef = firestore.collection("Community").document(communityId)
                val snapshot = communityRef.get().await()
                val members = snapshot.get("members") as? List<String> ?: emptyList()

                if (currentUserId in members) {
                    callback(true, null)
                } else {
                    callback(false, "You must join the community first")
                }
            } catch (e: Exception) {
                callback(false, "Error checking membership: ${e.message}")
            }
        }
    }

    /**
     * Attempt to join an event with membership validation (keeping original method for backward compatibility)
     */
    fun joinEvent(
        communityId: String,
        eventId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Use the enhanced method
        joinEventWithMembershipCheck(communityId, eventId, onSuccess, onFailure)
    }

    /**
     * Leave an event
     */
    fun leaveEvent(
        communityId: String,
        eventId: String,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val currentUserId = firebaseAuth.currentUser?.uid ?: return@launch
                val eventRef = firestore.collection("Community")
                    .document(communityId)
                    .collection("events")
                    .document(eventId)

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(eventRef)
                    val joinedUsers = snapshot.get("joinedUsers") as? MutableList<String> ?: mutableListOf()

                    if (currentUserId in joinedUsers) {
                        joinedUsers.remove(currentUserId)
                        transaction.update(eventRef, "joinedUsers", joinedUsers)
                    }
                }.await()

                onSuccess()

            } catch (e: Exception) {
                Log.e("CommunityViewModel", "Failed to leave event: ${e.message}")
                onFailure(e.message ?: "Failed to leave event")
            }
        }
    }

    // ========== ENHANCED POST INTERACTION METHODS ==========

    /**
     * Enhanced like post with strict community membership validation
     */
    fun likePostWithMembershipCheck(
        post: PostUiModel,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val currentUserId = firebaseAuth.currentUser?.uid ?: run {
                    onFailure("User not authenticated")
                    return@launch
                }

                // First, verify community membership from Firestore (server-side validation)
                val communityRef = firestore.collection("Community").document(post.communityId)
                val communitySnapshot = communityRef.get().await()
                val members = communitySnapshot.get("members") as? List<String> ?: emptyList()

                if (currentUserId !in members) {
                    onFailure("You must join the community first to like posts")
                    return@launch
                }

                // If user is a member, proceed with liking the post
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

                onSuccess()

            } catch (e: Exception) {
                Log.e("CommunityViewModel", "Failed to like post: ${e.message}")
                onFailure("Failed to like post: ${e.message}")
            }
        }
    }

    /**
     * Enhanced method to check if user can interact with posts
     */
    fun canUserInteractWithPosts(communityId: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val currentUserId = firebaseAuth.currentUser?.uid ?: run {
                    callback(false, "User not authenticated")
                    return@launch
                }

                val communityRef = firestore.collection("Community").document(communityId)
                val snapshot = communityRef.get().await()
                val members = snapshot.get("members") as? List<String> ?: emptyList()

                if (currentUserId in members) {
                    callback(true, null)
                } else {
                    callback(false, "You must join the community first")
                }
            } catch (e: Exception) {
                callback(false, "Error checking membership: ${e.message}")
            }
        }
    }

    /**
     * Enhanced comment on post with membership validation
     */
    fun commentOnPostWithMembershipCheck(
        postId: String,
        communityId: String,
        comment: String,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val currentUserId = firebaseAuth.currentUser?.uid ?: run {
                    onFailure("User not authenticated")
                    return@launch
                }

                // Check community membership
                val communityRef = firestore.collection("Community").document(communityId)
                val communitySnapshot = communityRef.get().await()
                val members = communitySnapshot.get("members") as? List<String> ?: emptyList()

                if (currentUserId !in members) {
                    onFailure("You must be a community member to comment on posts")
                    return@launch
                }

                // Add comment logic here
                val commentData = mapOf(
                    "userId" to currentUserId,
                    "comment" to comment,
                    "timestamp" to System.currentTimeMillis()
                )

                firestore.collection("Community")
                    .document(communityId)
                    .collection("posts")
                    .document(postId)
                    .collection("comments")
                    .add(commentData)
                    .await()

                onSuccess()

            } catch (e: Exception) {
                Log.e("CommunityViewModel", "Failed to comment: ${e.message}")
                onFailure(e.message ?: "Failed to comment")
            }
        }
    }

    /**
     * Original like post method (keeping for backward compatibility)
     * Now uses enhanced validation
     */
    fun likePost(post: PostUiModel) {
        likePostWithMembershipCheck(
            post = post,
            onSuccess = {
                // Post liked successfully
            },
            onFailure = { error ->
                Log.e("CommunityViewModel", "Failed to like post: $error")
                _error.value = error
            }
        )
    }

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
                val result = createCommunityUseCase(name=name, description=description,
                    category=category, imageUri=imageUri, currentUserId)
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

    // Fetches the full detail of a single community by its ID and checks membership
    fun fetchCommunityDetail(communityId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = getCommunityByIdUseCase(communityId)
            result.onSuccess {
                _communityDetail.value = it
                // Check membership status when community details are loaded
                checkMembershipStatus(communityId)
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

    fun updateCommunity(
        communityId: String,
        name: String,
        description: String,
        category : String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _loading.value = true
            _actionType.value = "update"
            try {
                val result = updateCommunityUseCase(communityId, name, description,category, imageUri)
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

    fun deleteCommunity(communityId: String, imageUrl: String?) {
        viewModelScope.launch {
            _loading.value = true
            _actionType.value = "delete"
            try {
                val result = deleteCommunityUseCase(communityId, imageUrl)
                result.onSuccess {
                    _success.value = true
                }.onFailure {
                    _error.value = "Failed to delete community: ${it.message}"
                }
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }
}
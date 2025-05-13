package com.example.sawaapplication.screens.home.presentation.vmModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawaapplication.screens.post.domain.model.Post
import com.example.sawaapplication.screens.post.domain.useCases.GetAllPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllPostsUseCase: GetAllPostsUseCase
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _communityNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val communityNames: StateFlow<Map<String, String>> = _communityNames

    private val _userNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val userNames: StateFlow<Map<String, String>> = _userNames

    private val _userDetails = MutableStateFlow<Map<String, Pair<String, String>>>(emptyMap())
    val userDetails: StateFlow<Map<String, Pair<String, String>>> = _userDetails

    // Map Post object to its Firestore document ID
    private val _postDocumentIds = MutableStateFlow<Map<Post, String>>(emptyMap())
    val postDocumentIds: StateFlow<Map<Post, String>> = _postDocumentIds


    fun fetchAllPosts() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val snapshot = firestore.collectionGroup("posts").get().await()

                val postsList = mutableListOf<Post>()
                val docIdMap = mutableMapOf<Post, String>()

                for (doc in snapshot.documents) {
                    val post = doc.toObject(Post::class.java)
                    if (post != null) {
                        postsList.add(post)
                        docIdMap[post] = doc.id
                    }
                }

                _posts.value = postsList
                _postDocumentIds.value = docIdMap

                val communityIds = postsList.map { it.communityId }.distinct()
                val userIds = postsList.map { it.userId }.distinct()

                fetchCommunityNames(communityIds)
                fetchUserDetails(userIds)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }


    private suspend fun fetchCommunityNames(communityIds: List<String>) = coroutineScope {
        try {
            val namesList = communityIds.map { id ->
                async {
                    try {
                        val snapshot = firestore.collection("Community").document(id).get().await()
                        if (snapshot.exists()) {
                            val name = snapshot.getString("name") ?: "Unknown"
                            id to name
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("Firebase", "Error fetching community $id: ${e.message}")
                        null
                    }
                }
            }.awaitAll()

            val namesMap = namesList.filterNotNull().toMap()
            _communityNames.value = namesMap

        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching community names: ${e.message}")
        }
    }

    private suspend fun fetchUserDetails(userIds: List<String>) = coroutineScope {
        try {
            val detailsList = userIds.map { id ->
                async {
                    try {
                        val snapshot = firestore.collection("User").document(id).get().await()
                        if (snapshot.exists()) {
                            val name = snapshot.getString("name") ?: "Unknown"
                            val image = snapshot.getString("image") ?: ""
                            id to (name to image)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("Firebase", "Error fetching user $id: ${e.message}")
                        null
                    }
                }
            }.awaitAll()

            val detailsMap = detailsList.filterNotNull().toMap()
            _userDetails.value = detailsMap

        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching user details: ${e.message}")
        }
    }

    // Like functionality: Increase likes in FireStore
    fun likePost(post: Post) {
        viewModelScope.launch {
            try {
                val docId = postDocumentIds.value[post]
                if (docId == null) {
                    Log.e("HomeViewModel", "Document ID not found for post")
                    return@launch
                }

                val postRef = firestore
                    .collection("Community")
                    .document(post.communityId)
                    .collection("posts")
                    .document(docId)

                val snapshot = postRef.get().await()
                val currentLikes = snapshot.getLong("likes") ?: 0
                val newLikes = if (currentLikes == 1L) 0 else 1

                firestore.runTransaction { transaction ->
                    transaction.update(postRef, "likes", newLikes)
                }.await()

                val updatedPost = post.copy(likes = newLikes.toInt())
                _posts.value = _posts.value.map {
                    if (it == post) updatedPost else it
                }

                // Also update the post ID mapping to match the new object
                _postDocumentIds.value = _postDocumentIds.value.toMutableMap().apply {
                    remove(post)
                    put(updatedPost, docId)
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to like post: ${e.message}")
            }
        }
    }
}
package com.example.sawaapplication.screens.post.presentation.vmModels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawaapplication.screens.post.domain.model.Post
import com.example.sawaapplication.screens.post.domain.useCases.CreatePostUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel(){

    var communityId by mutableStateOf<String?>("")
    var content by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)


    private val uid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    private val username: String
        get() = firebaseAuth.currentUser?.displayName ?: "Anonymous"

    private val userIcon: String
        get() = firebaseAuth.currentUser?.photoUrl?.toString() ?: ""

    fun createPost(communityId: String) {
        if (communityId.isBlank()) {
            Log.e("CreatePost", "Missing communityId. Cannot create post.")
            return
        }

        val postContent = content.trim()

        if (postContent.isBlank() && imageUri == null) {
            Log.e("CreatePost", "Post must have content or image.")
            return
        }

        val post = Post(
            content = postContent,
            createdAt = Date().toString(),
            imageUri = "", // Will be updated after upload
            username = username,
            userIcon = userIcon,
            communityId = communityId,


        )

        viewModelScope.launch {
            try {
                Log.d("CreatePost", "Creating post in communityId: $communityId")
                createPostUseCase(communityId, post, imageUri)
                Log.d("CreatePost", "Post created successfully")

                // Reset form after successful creation
                content = ""
                imageUri = null

            } catch (e: Exception) {
                Log.e("CreatePost", "Error creating post: ${e.message}", e)
            }
        }
    }

}
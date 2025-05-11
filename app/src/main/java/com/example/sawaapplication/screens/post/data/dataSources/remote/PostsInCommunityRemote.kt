package com.example.sawaapplication.screens.post.data.dataSources.remote

import android.net.Uri
import android.util.Log
import com.example.sawaapplication.screens.post.domain.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class PostsInCommunityRemote @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth : FirebaseAuth
) {
    suspend fun createPostInCommunity(
        communityId: String,
        post: Post,
        imageUri: Uri?
    ) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            Log.e("Firebase", "User is not authenticated")
            return
        }

        try {
            var imageUrl = ""

            // Upload image to Firebase Storage if provided
            if (imageUri != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                    .child("postImages/${user.uid}_${System.currentTimeMillis()}.jpg")

                storageRef.putFile(imageUri).await()
                imageUrl = storageRef.downloadUrl.await().toString()
            }

            // Create updated post with the image URL if an image was uploaded
            val updatedPost = post.copy(
                imageUri = imageUrl,
                createdAt = Date().toString()
            )

            // Reference to Firestore subcollection for posts
            firestore.collection("Community")
                .document(communityId)
                .collection("posts")
                .document()
                .set(updatedPost, SetOptions.merge())

            Log.d("Firebase", "Post created successfully in community: $communityId")

        } catch (e: Exception) {
            // Handle any errors
            Log.e("Firebase", "Error creating post: ${e.message}")
        }
    }

}
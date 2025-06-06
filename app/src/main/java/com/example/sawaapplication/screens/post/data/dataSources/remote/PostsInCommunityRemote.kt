package com.example.sawaapplication.screens.post.data.dataSources.remote

import android.net.Uri
import android.util.Log
import com.example.sawaapplication.screens.post.domain.model.Post
import com.example.sawaapplication.screens.post.domain.model.PostUiModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
                userId = user.uid, // UserID
                imageUri = imageUrl,
                createdAt = Date().toString()
            )

            // Reference to Firestore sub-collection for posts
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

    suspend fun getPostsForCommunity(communityId: String): Result<List<PostUiModel>> {
        return try {
            val snapshot = firestore.collection("Community")
                .document(communityId)
                .collection("posts")
                .get()
                .await()
            val parser = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)

            val uiModels = snapshot.documents.mapNotNull { doc ->
                val post = doc.toObject(Post::class.java) ?: return@mapNotNull null

                val userDoc = firestore.collection("User")
                    .document(post.userId)
                    .get()
                    .await()

                val username = userDoc.getString("name") ?: "Unknown"
                val avatarUrl = userDoc.getString("image") ?: ""

                PostUiModel(
                    id = post.id,
                    username = username,
                    userAvatarUrl = avatarUrl,
                    postImageUrl = post.imageUri,
                    content = post.content,
                    likes = post.likes,
                    likedBy = post.likedBy,
                    userId = post.userId,
                    communityId = post.communityId,
                    createdAt = post.createdAt
                    )
            }.sortedByDescending { parser.parse(it.createdAt) ?: Date(0) }

            Result.success(uiModels)
        } catch (e: Exception) {
            Log.e("PostRepository", "Error fetching posts: ${e.message}", e)
            Result.failure(e)
        }

    }

}



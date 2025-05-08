package com.example.sawaapplication.screens.communities.data.dataSources.remote

import android.net.Uri
import android.util.Log
import com.example.sawaapplication.screens.communities.domain.model.Community
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommunityRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth

) {
    suspend fun createCommunity(community: Community, imageUri: Uri): Result<Unit> {
        val user =
            firebaseAuth.currentUser ?: return Result.failure(Exception("User not authenticated"))

        return try {
            val storageRef =
                FirebaseStorage.getInstance().reference.child("communityImages/${user.uid}_${System.currentTimeMillis()}.jpg")

            // Upload image
            storageRef.putFile(imageUri).await()

            // Get image URL
            val imageUrl = storageRef.downloadUrl.await()

            // Update community with image URL and creatorId
            val updatedCommunity = community.copy(image = imageUrl.toString())

            // Save to Firestore
            val communityRef = firestore.collection("Community").add(updatedCommunity).await()


            Log.d("Firestore", "Community created with ID: ${communityRef.id}")

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchCommunity(userId: String): Result<List<Community>> {
        return try {
            val snapshot = FirebaseFirestore.getInstance().collection("Community")
                .whereArrayContains("members", userId).get().await()

            val communities = snapshot.documents.mapNotNull { it.toObject(Community::class.java) }
            Result.success(communities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


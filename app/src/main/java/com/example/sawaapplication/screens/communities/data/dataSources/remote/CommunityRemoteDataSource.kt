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

            val communities = snapshot.documents.mapNotNull { document ->
                document.toObject(Community::class.java)?.copy(id = document.id) // added `.copy(id = document.id)` to set Firestore document ID into the model
            }
            Result.success(communities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun joinCommunity(communityId: String, userId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("Community").document(communityId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val members = snapshot.get("members") as? List<String> ?: emptyList()
                if (!members.contains(userId)) {
                    val updatedMembers = members + userId
                    transaction.update(docRef, "members", updatedMembers)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCommunity(
        communityId: String,
        name: String,
        description: String,
        category : String,
        imageUri: Uri?
    ): Result<Unit> {
        return try {
            val docRef = firestore.collection("Community").document(communityId)
            val updates = mutableMapOf<String, Any>(
                "name" to name,
                "description" to description,
                "category" to category
            )

            // If a new image was selected
            if (imageUri != null) {
                try {
                    val imageRef = FirebaseStorage.getInstance().reference
                        .child("communityImages/${firebaseAuth.currentUser?.uid}_${System.currentTimeMillis()}.jpg")

                    imageRef.putFile(imageUri).await()
                    val newImageUrl = imageRef.downloadUrl.await().toString()

                    updates["image"] = newImageUrl
                }catch (e: Exception) {
                    Log.e("Firestore", "Image upload failed: ${e.message}", e)
                    return Result.failure(e)
                }
            }
            docRef.update(updates).await()
            Log.d("Firestore", "Community $communityId updated")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Firestore", "Update failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteCommunity(communityId: String, imageUrl: String?): Result<Unit> {

        return try {

            deleteSubCollection(communityId, "posts")
            deleteSubCollection(communityId, "event")
            deleteSubCollection(communityId, "messages")

            if (!imageUrl.isNullOrBlank()) {
                try {
                    val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                    imageRef.delete().await()
                } catch (e: Exception) {
                    // If the image doesn't exist, log and move on
                    Log.w("DeleteCommunity", "Image not found or already deleted: ${e.message}")
                }
            }

            FirebaseFirestore.getInstance()
                .collection("Community")
                .document(communityId)
                .delete()
                .await()

            Log.d("DeleteCommunity", "Community $communityId deleted")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DeleteCommunity", "Failed to delete community: ${e.message}", e)
            Result.failure(e)
        }

    }

    // To delete all the "posts" "events" and "chats" that are related to a community
    suspend fun deleteSubCollection(communityId: String, subCollection: String) {
        val collectionRef = firestore.collection("Community")
            .document(communityId)
            .collection(subCollection)

        val documents = collectionRef.get().await()
        for (doc in documents) {
            doc.reference.delete().await()
        }
    }

}


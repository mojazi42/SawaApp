package com.example.sawaapplication.screens.profile.dataSources.remote

import android.net.Uri
import android.util.Log
import com.example.sawaapplication.screens.profile.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore

) {

    suspend fun fetchAboutMe(userId: String): String? {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("User")
                .document(userId)
                .get()
                .await()  // suspend-friendly, no callback

            snapshot.getString("aboutMe")
        } catch (e: Exception) {
            Log.e("FirestoreRemoteDataSource", "Error fetching aboutMe", e)
            null
        }
    }
    suspend fun fetchUserName(userId: String): String? {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("User")
                .document(userId)
                .get()
                .await()  // This makes it suspend-friendly

            snapshot.getString("name")
        } catch (e: Exception) {
            Log.e("RemoteDataSource", "Error fetching user name", e)
            null
        }
    }
    suspend fun fetchProfileImageUrl(userId: String): String? {
        return try {
            val document = FirebaseFirestore.getInstance()
                .collection("User")
                .document(userId)
                .get()
                .await()  // suspend function, no callbacks

            if (document.exists()) {
                document.getString("image")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirestoreRemoteDataSource", "Error fetching profile image url", e)
            null
        }
    }
    suspend fun uploadProfileImage(uri: Uri): Boolean {
        return try {
            val user = FirebaseAuth.getInstance().currentUser ?: return false
            val storageRef = FirebaseStorage.getInstance().reference
                .child("profileImages/${user.uid}.jpg")

            // Upload the file
            storageRef.putFile(uri).await()

            // Get the download URL
            val downloadUri = storageRef.downloadUrl.await()

            // Update the Firestore document with the image URL
            FirebaseFirestore.getInstance()
                .collection("User")
                .document(user.uid)
                .update("image", downloadUri.toString())
                .await()

            true
        } catch (e: Exception) {
            Log.e("FirestoreRemoteDataSource", "Failed to upload profile image", e)
            false
        }
    }
    suspend fun fetchUserById(userId: String): User? {
        return try {
            val snapshot = firestore.collection("User").document(userId).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}


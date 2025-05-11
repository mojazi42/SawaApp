package com.example.sawaapplication.screens.event.data.dataSources

import android.net.Uri
import android.util.Log
import com.example.sawaapplication.screens.event.domain.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class EventInCommunityRemote @Inject constructor(
    private val firestore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth
) {
    suspend fun createEventInCommunity(
        communityId: String, event: Event, imageUri: Uri
    ) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            Log.e("Firebase", "User is not authenticated")
            return
        }

        val storageRef =
            FirebaseStorage.getInstance().reference.child("eventImages/${user.uid}_${System.currentTimeMillis()}.jpg")

        try {
            // Upload image to Firebase Storage
            storageRef.putFile(imageUri).await()

            // Get the download URL of the uploaded image
            val imageUrl = storageRef.downloadUrl.await()

            // Create updated event with the image URL
            val updatedEvent = event.copy(imageUri = imageUrl.toString())

            // Reference to Firestore subcollection for events
            firestore.collection("Community")  // Ensure consistency in collection name
                .document(communityId).collection("events").document()
                .set(updatedEvent, SetOptions.merge()) // Create a new document


            Log.d("Firebase", "Event created successfully in community: $communityId")

        } catch (e: Exception) {
            // Handle any errors
            Log.e("Firebase", "Error creating event: ${e.message}")
        }
    }


}

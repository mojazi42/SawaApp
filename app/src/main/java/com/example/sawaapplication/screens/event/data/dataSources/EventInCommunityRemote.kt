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
                .document(communityId).collection("event").document()
                .set(updatedEvent, SetOptions.merge())


            Log.d("Firebase", "Event created successfully in community: $communityId")

        } catch (e: Exception) {
            // Handle any errors
            Log.e("Firebase", "Error creating event: ${e.message}")
        }
    }

    suspend fun fetchEventsFromCommunity(communityId: String): Result<List<Event>> {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("Community")
                .document(communityId)
                .collection("event")
                .get()
                .await()

            val events = snapshot.documents.mapNotNull { document ->
                document.toObject(Event::class.java)?.copy(id = document.id) // <-- include doc ID
            }

            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinEvent(communityId: String, eventId: String, userId: String): Result<Unit> {
        return try {
            val eventRef = firestore.collection("Community")
                .document(communityId)
                .collection("event")
                .document(eventId)

            // Use arrayUnion to avoid duplicate entries
            eventRef.update(
                "joinedUsers",
                com.google.firebase.firestore.FieldValue.arrayUnion(userId)
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun leaveEvent(communityId: String, eventId: String, userId: String): Result<Unit> {
        return try {
            val eventRef = firestore.collection("Community")
                .document(communityId)
                .collection("event")
                .document(eventId)

            eventRef.update(
                "joinedUsers",
                com.google.firebase.firestore.FieldValue.arrayRemove(userId)
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
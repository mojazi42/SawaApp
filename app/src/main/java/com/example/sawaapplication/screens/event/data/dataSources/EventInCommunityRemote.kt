package com.example.sawaapplication.screens.event.data.dataSources

import android.net.Uri
import android.util.Log
import com.example.sawaapplication.screens.event.domain.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventInCommunityRemote @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    // Helper to get reference to the event subcollection inside a community
    private fun eventCollectionRef(communityId: String) =
        firestore.collection("Community").document(communityId).collection("event")

    /**
     * Creates a new event in a community with image upload.
     */
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

    /**
     * Fetch all events from a specific community.
     */
    suspend fun fetchEventsFromCommunity(communityId: String): Result<List<Event>> {
        return try {
            val snapshot = eventCollectionRef(communityId).get().await()

            val events = snapshot.documents.mapNotNull { document ->
                document.toObject(Event::class.java)?.copy(id = document.id)
            }

            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Adds the current user to the joinedUsers list of an event.
     */
    suspend fun joinEvent(communityId: String, eventId: String, userId: String): Result<Unit> {
        return try {
            eventCollectionRef(communityId)
                .document(eventId)
                .update("joinedUsers", FieldValue.arrayUnion(userId))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Removes the current user from the joinedUsers list of an event.
     */
    suspend fun leaveEvent(communityId: String, eventId: String, userId: String): Result<Unit> {
        return try {
            eventCollectionRef(communityId)
                .document(eventId)
                .update("joinedUsers", FieldValue.arrayRemove(userId))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes an event from the Firestore database.
     */
    suspend fun deleteEvent(communityId: String, eventId: String): Result<Unit> {
        return try {
            firestore.collection("Community")
                .document(communityId)
                .collection("event")
                .document(eventId)
                .delete()
                .await()
            Log.d("RemoteDelete", "Deleted $eventId from $communityId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RemoteDelete", "Failed to delete: ${e.message}")
            Result.failure(e)
        }
    }


    /**
     * Updates specific fields of an event document in Firestore.
     */
    suspend fun updateEvent(
        communityId: String,
        eventId: String,
        updatedData: Map<String, Any>
    ): Result<Unit> {
        return try {
            eventCollectionRef(communityId)
                .document(eventId)
                .update(updatedData)
                .await()

            Log.d("Firebase", "Event updated: $eventId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Firebase", "Error updating event: ${e.message}")
            Result.failure(e)
        }
    }
}

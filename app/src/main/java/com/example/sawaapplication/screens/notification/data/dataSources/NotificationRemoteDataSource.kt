package com.example.sawaapplication.screens.notification.data.dataSources

import android.util.Log
import com.example.sawaapplication.BuildConfig
import com.example.sawaapplication.screens.notification.domain.model.Notification
import com.example.sawaapplication.screens.post.domain.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class NotificationRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    // Fetches all user notifications
    fun fetchNotifications(listener: (List<Notification>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return  // Get current user ID or exit if null

        firestore.collection("Notification")  // Access 'Notification' collection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING) // Order by newest first
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("RemoteDataSource", "Error: ${error.message}")
                    return@addSnapshotListener
                }

                val notifications = snapshot
                    ?.documents
                    //filter out any reminder docs
                    ?.filter { doc ->
                        doc.getString("type") != "event_reminder"
                    }
                    ?.mapNotNull { doc ->
                        val message = doc.getString("message") ?: return@mapNotNull null
                        val timestamp =
                            doc.getTimestamp("timestamp")?.toDate() ?: return@mapNotNull null
                        val userId = doc.getString("userId") ?: return@mapNotNull null
                        val isRead = doc.getBoolean("isRead") ?: false
                        Notification(doc.id, message, timestamp, userId, isRead)
                    } ?: emptyList()

                listener(notifications)
            }
    }

    // Marks all unread notifications for the current user as read
    fun markAllAsRead() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("Notification")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .get()
            .addOnSuccessListener { snapshot ->
                // Update all unread notifications to isRead = true
                snapshot.documents.forEach { it.reference.update("isRead", true) }
            }
    }

    // Sends reminders to community members about upcoming events
    fun remindCommunityMembersAboutUpcomingEvent() {
        val currentTime = System.currentTimeMillis()

        // Get all communities
        firestore.collection("Community")
            .get()
            .addOnSuccessListener { communitySnapshot ->
                communitySnapshot.documents.forEach { communityDoc ->
                    val communityId = communityDoc.id
                    val communityName = communityDoc.getString("name") ?: "Unknown"
                    val members = communityDoc.get("members") as? List<String> ?: emptyList()

                    // Get all events in this community
                    firestore.collection("Community").document(communityId)
                        .collection("event")
                        .get()
                        .addOnSuccessListener { eventsSnapshot ->
                            eventsSnapshot.documents.forEach { eventDoc ->
                                val eventTitle = eventDoc.getString("title") ?: "No Title"
                                val eventTimestamp = eventDoc.getTimestamp("time")?.toDate()?.time
                                val creatorId = eventDoc.getString("creatorId")

                                if (eventTimestamp == null) {
                                    Log.d(
                                        "NotifDebug",
                                        "Event '$eventTitle' has no valid time, skipping"
                                    )
                                    return@forEach
                                }

                                val timeDiff = eventTimestamp - currentTime

                                // Check if the event is within the next 24 hours
                                if (timeDiff in 0..86400000L) {
                                    val message =
                                        "Reminder: The event '$eventTitle' in '$communityName' has one day left to start!"

                                    // Run transaction to ensure it only send once
                                    firestore.runTransaction { transaction ->
                                        val snapshot = transaction.get(eventDoc.reference)
                                        val reminderSent =
                                            snapshot.getBoolean("reminderSent") ?: false

                                        if (reminderSent) {
                                            Log.d(
                                                "NotifDebug",
                                                "Reminder already sent for '$eventTitle', skipping."
                                            )
                                            return@runTransaction null
                                        }

                                        Log.d(
                                            "NotifDebug",
                                            "Sending notifications for event '$eventTitle'"
                                        )

                                        // Send notifications to all members except the creator
                                        members.filter { it != creatorId }.forEach { memberId ->
                                            sendNotificationToUser(memberId, message)
                                            sendPushNotificationToUser(memberId, message)
                                        }

                                        // Mark reminder as sent
                                        transaction.update(eventDoc.reference, "reminderSent", true)
                                        null
                                    }.addOnFailureListener { e ->
                                        Log.e(
                                            "NotifDebug",
                                            "Transaction failed for '$eventTitle': ${e.message}"
                                        )
                                    }
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                "NotifDebug",
                                "Failed to get events for community $communityId: ${e.message}"
                            )
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("NotifDebug", "Failed to get communities: ${e.message}")
            }
    }

    // Saves an in-app notification to FireStore
    fun sendNotificationToUser(userId: String, message: String) {
        val data = mapOf(
            "message" to message,
            "timestamp" to FieldValue.serverTimestamp(),
            "userId" to userId,
            "isRead" to false
        )
        firestore.collection("Notification").add(data)
    }

    // Sends a OneSignal push notification using player ID from user document
    fun sendPushNotificationToUser(userId: String, message: String) {
        firestore.collection("User").document(userId).get()
            .addOnSuccessListener { doc ->
                val playerId = doc.getString("oneSignalPlayerId")

                if (playerId.isNullOrEmpty()) {
                    Log.e("NotifDebug", "No playerId found for user $userId")
                    return@addOnSuccessListener
                }

                Log.d("NotifDebug", "Found playerId $playerId for user $userId")

                // Build OneSignal push notification payload
                val url = "https://onesignal.com/api/v1/notifications"
                val json = JSONObject().apply {
                    put("app_id", BuildConfig.ONESIGNAL_KEY)
                    put("include_player_ids", JSONArray().put(playerId))
                    put("contents", JSONObject().put("en", message))
                }

                val requestBody = json.toString().toRequestBody("application/json".toMediaType())

                // Create HTTP request using OkHttp
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader(
                        "Authorization",
                        "Bearer ${BuildConfig.ONESIGNAL_AUTH_TOKEN}"
                    )
                    .build()

                OkHttpClient().newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        Log.e("NotifDebug", "Failed to send push: ${e.message}")
                    }

                    override fun onResponse(call: okhttp3.Call, response: Response) {
                        response.use {
                            Log.d("NotifDebug", "Push response: ${it.body?.string()}")
                        }
                    }
                })
            }
            .addOnFailureListener { e ->
                Log.e("NotifDebug", "Failed to fetch user document: ${e.message}")
            }
    }

    // Sends a notification to all community members about a newly created event
    fun sendCommunityEventNotification(communityId: String, eventName: String) {
        val currentUser = auth.currentUser ?: return
        val db = firestore

        db.collection("Community").document(communityId)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: "Unknown"
                val members =
                    doc.get("members")?.let { it as? List<*> }?.mapNotNull { it as? String }
                        ?: return@addOnSuccessListener

                val msg = "A new event \"$eventName\" was created in \"$name\" community."
                // Notify all members except the current user
                members.filter { it != currentUser.uid }.forEach { memberId ->
                    sendNotificationToUser(memberId, msg)
                }
            }
    }

    // Sends a notification when a post is liked
    fun sendLikeNotification(post: Post) {
        val likerId = post.likedBy.lastOrNull() ?: return
        val recipientId = post.userId

        // Get name of liker
        firestore.collection("User").document(likerId).get().addOnSuccessListener { userDoc ->
            val likerName = userDoc.getString("name") ?: "Someone"

            // Get community name and send notification
            firestore.collection("Community").document(post.communityId).get()
                .addOnSuccessListener { communityDoc ->
                    val communityName = communityDoc.getString("name") ?: "Unknown"
                    val message = "$likerName liked your post in '$communityName' community."
                    sendNotificationToUser(recipientId, message)
                }
        }
    }

    // Observes if there are any unread notifications for the current user
    fun observeUnreadNotifications(listener: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("Notification")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .addSnapshotListener { snapshot, _ ->
                listener(snapshot?.isEmpty == false) // Notify if any unread
            }
    }

    // Event Reminder, Fetch reminders (start + 1 hour passed, not yet responded)
    suspend fun getPendingReminders(userId: String): List<Notification> {
        val snapshot = firestore.collection("Notification")
            .whereEqualTo("userId", userId)
            .whereEqualTo("type", "event_reminder")
            .get()
            .await()

        val now = System.currentTimeMillis()
        return snapshot.documents.mapNotNull { doc ->
            val responded = doc.getBoolean("responded") ?: false
            if (responded) return@mapNotNull null

            val startTs = doc.getTimestamp("startTime")?.toDate()?.time
                ?: return@mapNotNull null
            if (startTs + 60 * 60 * 1000 > now) return@mapNotNull null

            val message = doc.getString("message") ?: return@mapNotNull null

            // Send OneSignal push notification reminder to the user
            sendPushNotificationToUser(userId, message)

            Notification(
                id = doc.id,
                message = message,
                timestamp = doc.getTimestamp("timestamp")!!.toDate(),
                userId = userId,
                isRead = doc.getBoolean("isRead") ?: false,
                type = "event_reminder",
                eventId = doc.getString("eventId"),
                startTime = doc.getTimestamp("startTime"),
                responded = false
            )
        }
    }

    // Mark attendance reminder responded & record
    suspend fun respondToReminder(
        userId: String,
        notificationId: String,
        attended: Boolean
    ) {
        // eventId from the reminder doc
        val docSnap = firestore.collection("Notification")
            .document(notificationId)
            .get()
            .await()
        val eventId = docSnap.getString("eventId")
            ?: throw IllegalStateException("Reminder $notificationId has no eventId")

        // responded = true, and if Yes, add eventId to attendance
        val batch = firestore.batch()
        val notifRef = firestore.collection("Notification").document(notificationId)
        batch.update(notifRef, "responded", true)

        if (attended) {
            val userRef = firestore.collection("User").document(userId)
            batch.update(
                userRef,
                "eventAttendance.eventIds",
                FieldValue.arrayUnion(eventId)
            )
        }
        batch.commit().await()
    }
}
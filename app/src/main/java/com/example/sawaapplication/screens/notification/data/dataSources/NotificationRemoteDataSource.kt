package com.example.sawaapplication.screens.notification.data.dataSources

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.sawaapplication.screens.notification.DelayedReminderWorker
import com.example.sawaapplication.screens.notification.domain.model.Notification
import com.example.sawaapplication.screens.post.domain.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    fun fetchNotifications(listener: (List<Notification>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("Notification")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("RemoteDataSource", "Error: ${error.message}")
                    return@addSnapshotListener
                }

                val notifications = snapshot?.documents?.mapNotNull { doc ->
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

    fun markAllAsRead() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("Notification")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { it.reference.update("isRead", true) }
            }
    }

    fun sendNotificationToUser(userId: String, message: String) {
        val data = mapOf(
            "message" to message,
            "timestamp" to FieldValue.serverTimestamp(),
            "userId" to userId,
            "isRead" to false
        )
        firestore.collection("Notification").add(data)
    }

    fun sendCommunityEventNotification(communityId: String, eventName: String) {
        val currentUser = auth.currentUser ?: return
        val db = firestore

        db.collection("Community").document(communityId)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: "Unknown"
                val members = doc.get("members") as? List<String> ?: return@addOnSuccessListener

                val msg = "A new event \"$eventName\" was created in \"$name\" community."
                members.filter { it != currentUser.uid }.forEach { memberId ->
                    sendNotificationToUser(memberId, msg)
                }
            }
    }

    fun sendLikeNotification(post: Post) {
        val likerId = post.likedBy.lastOrNull() ?: return
        val recipientId = post.userId

        firestore.collection("User").document(likerId).get().addOnSuccessListener { userDoc ->
            val likerName = userDoc.getString("name") ?: "Someone"

            firestore.collection("Community").document(post.communityId).get()
                .addOnSuccessListener { communityDoc ->
                    val communityName = communityDoc.getString("name") ?: "Unknown"
                    val message = "$likerName liked your post in '$communityName' community."
                    sendNotificationToUser(recipientId, message)
                }
        }
    }

    fun observeUnreadNotifications(listener: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("Notification")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .addSnapshotListener { snapshot, _ ->
                listener(snapshot?.isEmpty == false)
            }
    }

    fun scheduleEventReminder(
        eventName: String,
        eventDateMillis: Long,
        eventTime: String,
        context: Context
    ) {
        val timeParts = eventTime.split(":")
        val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
        val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

        val calendar = Calendar.getInstance().apply {
            timeInMillis = eventDateMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // Subtract 1 day for reminder
        calendar.add(Calendar.DAY_OF_YEAR, -1)

        val notificationTimeMillis = calendar.timeInMillis
        val currentTimeMillis = System.currentTimeMillis()
        val delayMillis = notificationTimeMillis - currentTimeMillis

        if (delayMillis > 0) {
            val workRequest = OneTimeWorkRequestBuilder<DelayedReminderWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("eventName" to eventName))
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
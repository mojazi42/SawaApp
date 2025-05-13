package com.example.sawaapplication.screens.chat.data.dataSources.remote

import android.util.Log
import com.example.sawaapplication.screens.chat.domain.model.ChatUserInfo
import com.example.sawaapplication.screens.chat.domain.model.Message
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MassageRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun sendMessage(
        communityId: String,
        messageText: String,
        senderId: String,
    ): Result<Unit> {
        return try {
            val messageMap = mapOf(
                "text" to messageText,
                "senderId" to senderId,
                "createdAt" to FieldValue.serverTimestamp()
            )

            firestore.collection("Community")
                .document(communityId)
                .collection("messages")
                .add(messageMap)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeMessages(communityId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection("Community")
            .document(communityId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)

            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getLastMessageAndSender(communityId: String): Pair<String, ChatUserInfo>? =
        suspendCoroutine { cont ->
            val db = FirebaseFirestore.getInstance()

            db.collection("Community")
                .document(communityId)
                .collection("messages")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { snapshot ->
                    val doc = snapshot.documents.firstOrNull()
                    Log.d("Firestore", "Last msg doc: ${doc?.data}")
                    val message = doc?.toObject(Message::class.java)

                    if (message != null) {
                        db.collection("User").document(message.senderId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                val senderName = userDoc.getString("name")
                                val senderImage = userDoc.getString("image")
                                Log.d("Firestore", "Sender name: $senderName")
                                cont.resume(message.text to ChatUserInfo(senderName, senderImage))
                            }
                            .addOnFailureListener {
                                Log.e("Firestore", "Failed to get sender", it)
                                cont.resume(null)
                            }
                    } else {
                        Log.w("Firestore", "No message found.")
                        cont.resume(null)
                    }
                }
        }
}

package com.example.sawaapplication.screens.chat.domain.useCases

import android.util.Log
import com.example.sawaapplication.screens.chat.domain.model.ChatUserInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
// 1) Use case to load the `members` array and then each User doc
class GetCommunityMembersUseCase @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend operator fun invoke(communityId: String): List<ChatUserInfo> {
        // Read the Community document
        val communityDoc = firestore.collection("Community")
            .document(communityId)
            .get()
            .await()

        // Pull out the 'members' field (list of user IDs)
        val memberIds = communityDoc.get("members") as? List<String> ?: emptyList()

        // For each ID, fetch that user's name+image
        return memberIds.mapNotNull { userId ->
            try {
                val userDoc = firestore.collection("User")
                    .document(userId)
                    .get()
                    .await()
                val name = userDoc.getString("name")
                val image = userDoc.getString("image")
                if (name != null && image != null) ChatUserInfo(name, image) else null
            } catch (e: Exception) {
                null
            }
        }
    }
}
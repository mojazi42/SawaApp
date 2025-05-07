package com.example.sawaapplication.screens.communities.data.dataSources.remote

import com.example.sawaapplication.screens.communities.domain.model.Community
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommunityRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun createCommunity(community: Community): Result<Unit> {
        return try {
            firestore.collection("Community")
                .add(community)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

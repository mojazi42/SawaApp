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
    suspend fun fetchCommunity(userId: String): Result<List<Community>> {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("Community")
                .whereArrayContains("members", userId)
                .get()
                .await()

            val communities = snapshot.documents.mapNotNull { it.toObject(Community::class.java) }

            Result.success(communities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


package com.example.sawaapplication.screens.communities.data.repository

import android.net.Uri
import android.util.Log
import com.example.sawaapplication.screens.communities.data.dataSources.remote.CommunityRemoteDataSource
import javax.inject.Inject
import com.example.sawaapplication.screens.communities.domain.model.Community
import com.example.sawaapplication.screens.communities.domain.repository.CommunityRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CommunityRepositoryImpl @Inject constructor(
    private val remoteDataSource: CommunityRemoteDataSource,
    private val firestore: FirebaseFirestore
) : CommunityRepository {


    override suspend fun createCommunity(
        community: Community, imageUri: Uri
    ): Result<Unit> {
        return remoteDataSource.createCommunity(community, imageUri)
    }


    override suspend fun fetchCommunities(userId: String): Result<List<Community>> {
        return remoteDataSource.fetchCommunity(userId)
    }
    // Implements fetching a single community document from Firestore by its ID.
    // This function queries Firestore for a document with the provided communityId, converts it to a Community object,
    // copies the Firestore-generated document ID into the model, logs debug info, and returns the result.
    // It integrates the getCommunityById functionality into the repository layer to expose to ViewModel/UseCase.
    override suspend fun getCommunityById(communityId: String): Result<Community> {
        return try {
            val snapshot = firestore.collection("Community").document(communityId).get().await()
            if (snapshot.exists()) {
                val community = snapshot.toObject(Community::class.java)?.copy(id = snapshot.id)
                Log.d("DEBUG", "Firestore document exists: ${snapshot.exists()}")

                Result.success(community!!)
            } else {
                Result.failure(Exception("Community not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinCommunity(communityId: String, userId: String): Result<Unit> {
        return remoteDataSource.joinCommunity(communityId, userId)
    }
    override suspend fun leaveCommunity(communityId: String, userId: String): Result<Unit> {
        return try {
            val communityRef = firestore.collection("Community").document(communityId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(communityRef)
                val members = snapshot.get("members") as? MutableList<String> ?: mutableListOf()
                if (userId in members) {
                    members.remove(userId)
                    transaction.update(communityRef, "members", members)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCommunity(
        communityId: String,
        name: String,
        description: String,
        category : String,
        imageUri: Uri?
    ): Result<Unit> {
        return remoteDataSource.updateCommunity(communityId, name, description, category, imageUri)
    }

    override suspend fun deleteCommunity(communityId: String): Result<Unit> {
        return remoteDataSource.deleteCommunity(communityId)
    }


}


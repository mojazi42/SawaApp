package com.example.sawaapplication.screens.communities.presentation.vmModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.sawaapplication.screens.communities.domain.model.Community
import kotlinx.coroutines.tasks.await

@HiltViewModel
class ExploreCommunityViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    var searchText by mutableStateOf("")
    var communities by mutableStateOf<List<Community>>(emptyList())

    init {
        fetchCommunities()
    }

    private fun fetchCommunities() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("Community").get().await()
                communities = snapshot.documents.mapNotNull { document ->
                    try {
                        val community = Community(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            description = document.getString("description") ?: "",
                            image = document.getString("image") ?: "",
                            creatorId = document.getString("creatorId") ?: "",
                            members = document.get("members") as? List<String> ?: emptyList(),
                            createdAt = document.get("createdAt")?.toString() ?: "",
                            updatedAt = document.get("updatedAt")?.toString() ?: ""
                        )
                        community
                    } catch (e: Exception) {
                        Log.e(
                            "ExploreCommunityVM",
                            "Error parsing community data for document: ${document.id}",
                            e
                        )
                        null
                    }
                }

                communities = communities.sortedBy { it.createdAt.toLongOrNull() ?: Long.MAX_VALUE }

            } catch (e: Exception) {
                Log.e("ExploreCommunityVM", "Error fetching communities", e)
            }
        }
    }

    fun onSearchTextChange(newText: String) {
        searchText = newText
    }

    val filteredCommunities: List<Community>
        get() = if (searchText.isBlank()) communities
        else communities.filter {
            it.name.contains(searchText, ignoreCase = true)
        }
}
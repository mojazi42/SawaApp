package com.example.sawaapplication.screens.communities.presentation.vmModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExploreCommunityViewModel @Inject constructor() : ViewModel() {

    var searchText by mutableStateOf("")

    fun onSearchTextChange(newText : String){
        searchText = newText
    }

    private val allCommunities = listOf(
        "Saudi Community", "Tech Stars", "Green Team", "Fitness Club", "Art Lovers", "Book Worms"
    )

    val filteredCommunities: List<String>
        get() = if (searchText.isBlank()) allCommunities
        else allCommunities.filter {
            it.contains(searchText, ignoreCase = true)
        }
}


package com.example.sawaapplication.screens.communities.presentation.vmModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.sawaapplication.screens.communities.domain.useCases.CreateCommunityUseCase

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val createCommunityUseCase: CreateCommunityUseCase
) : ViewModel() {

    fun createCommunity(
        name: String,
        description: String,
        imageUri: Uri?,
        currentUserId: String
    ) {
        viewModelScope.launch {
            createCommunityUseCase(
                name = name,
                description = description,
                imageUri = imageUri,
                creatorId = currentUserId
            )
        }
    }

}



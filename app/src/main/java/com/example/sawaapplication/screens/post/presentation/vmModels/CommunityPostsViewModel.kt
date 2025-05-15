package com.example.sawaapplication.screens.post.presentation.vmModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawaapplication.screens.post.domain.model.Post
import com.example.sawaapplication.screens.post.domain.model.PostUiModel
import com.example.sawaapplication.screens.post.domain.useCases.GetCommunityPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityPostsViewModel @Inject constructor(
    private val getPostsByCommunityIdUseCase: GetCommunityPostsUseCase
) : ViewModel() {
    var posts by mutableStateOf<List<PostUiModel>>(emptyList())

        private set

    fun loadPosts(communityId: String) {
        viewModelScope.launch {
            posts = getPostsByCommunityIdUseCase(communityId).getOrElse { emptyList() }
        }
    }
}

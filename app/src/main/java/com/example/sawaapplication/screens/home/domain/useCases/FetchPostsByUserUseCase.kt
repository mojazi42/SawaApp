package com.example.sawaapplication.screens.home.domain.useCases

import com.example.sawaapplication.screens.home.domain.repository.HomeRepository
import com.example.sawaapplication.screens.post.domain.model.Post
import javax.inject.Inject

class FetchPostsByUserUseCase @Inject constructor(
    private val repository: HomeRepository
)  {
    suspend operator fun invoke(userId:String):Pair<List<Post>, Map<Post, String>>{
        return repository.fetchPostsByUser(userId)
    }
}
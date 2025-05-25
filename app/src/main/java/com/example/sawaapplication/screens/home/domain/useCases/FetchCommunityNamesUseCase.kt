package com.example.sawaapplication.screens.home.domain.useCases

import com.example.sawaapplication.screens.home.domain.repository.HomeRepository
import com.example.sawaapplication.screens.post.domain.model.Post
import javax.inject.Inject


class FetchCommunityNamesUseCase @Inject constructor(
    private val repository: HomeRepository
)  {
    suspend operator fun invoke(id:List<String>): Map<String, String> {
        return repository.fetchCommunityNames(id)
    }
}
package com.example.sawaapplication.screens.home.domain.useCases

import com.example.sawaapplication.screens.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetUserCommunityIdsUseCase @Inject constructor(
    private val repository: HomeRepository
)  {
    suspend operator fun invoke(userId : String): List<String> {
        return repository.getUserCommunityIds(userId)
    }
}
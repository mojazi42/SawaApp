package com.example.sawaapplication.screens.home.domain.useCases

import com.example.sawaapplication.screens.home.domain.repository.HomeRepository
import javax.inject.Inject


class FetchUserDetailsUseCase @Inject constructor(
    private val repository: HomeRepository
)  {
    suspend operator fun invoke(userId:List<String>): Map<String, Pair<String, String>> {
        return repository.fetchUserDetails(userId)
    }
}
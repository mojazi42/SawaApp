//package com.example.sawaapplication.screens.communities.domain.useCases
//
//import com.example.sawaapplication.screens.communities.domain.repository.CommunityRepository
//import javax.inject.Inject
//
//class CreateCommunityUseCase @Inject constructor(private val repository: CommunityRepository) {
//    suspend operator fun invoke(
//        name: String,
//        description: String,
//        img: String,
//    ): Result<Unit> {
//        return repository.createCommunity(name, description, img)
//    }
//}
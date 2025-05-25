package com.example.sawaapplication.screens.profile.domain.useCases

import com.example.sawaapplication.screens.profile.domain.model.Badge
import com.example.sawaapplication.screens.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetAwardedBadges @Inject constructor(
    private val repository: ProfileRepository
){
    suspend operator  fun invoke(userId:String):List<Badge> {
        return repository.getAwardedBadges(userId)
    }
}
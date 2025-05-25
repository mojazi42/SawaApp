package com.example.sawaapplication.screens.profile.domain.useCases

import com.example.sawaapplication.screens.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GrantBadgeUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(userId: String) {
        val total = repository.getAttendedEvent(userId).size
        val earned = buildList {
            if (total >= 30) add("1_month")
            if (total >= 21) add("3_weeks")
            if (total >= 14) add("2_weeks")
            if (total >=  7) add("1_week")
            if (total >=  3) add("3_days")
        }
        repository.grantBadges(userId, earned)
    }
}
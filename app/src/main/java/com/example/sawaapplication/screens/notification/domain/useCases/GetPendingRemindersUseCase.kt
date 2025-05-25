package com.example.sawaapplication.screens.notification.domain.useCases

import com.example.sawaapplication.screens.notification.domain.model.Notification
import com.example.sawaapplication.screens.notification.domain.repository.NotificationRepository
import javax.inject.Inject

class GetPendingRemindersUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(userId: String): List<Notification> =
        repository.getPendingReminders(userId)
}

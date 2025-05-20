package com.example.sawaapplication.screens.notification.domain.useCases

import android.content.Context
import com.example.sawaapplication.screens.notification.domain.repository.NotificationRepository
import javax.inject.Inject

class ScheduleEventReminderUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(eventName: String, eventDateMillis: Long, eventTime: String, context: Context) {
        repository.scheduleEventReminder(eventName, eventDateMillis, eventTime, context)
    }
}

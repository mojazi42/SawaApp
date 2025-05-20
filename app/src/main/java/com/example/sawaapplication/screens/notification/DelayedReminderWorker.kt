package com.example.sawaapplication.screens.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class DelayedReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val eventName = inputData.getString("eventName") ?: return Result.failure()

        val service = ReminderNotificationService(context)
        service.showNotification(eventName)

        return Result.success()
    }
}

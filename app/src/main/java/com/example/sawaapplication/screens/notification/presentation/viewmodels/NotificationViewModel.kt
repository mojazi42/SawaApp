package com.example.sawaapplication.screens.notification.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawaapplication.core.sharedPreferences.NotificationPreferences
import com.example.sawaapplication.screens.notification.domain.model.Notification
import com.example.sawaapplication.screens.notification.domain.useCases.FetchNotificationsUseCase
import com.example.sawaapplication.screens.notification.domain.useCases.GetPendingRemindersUseCase
import com.example.sawaapplication.screens.notification.domain.useCases.MarkNotificationsAsReadUseCase
import com.example.sawaapplication.screens.notification.domain.useCases.NotifyCommunityOfEventUseCase
import com.example.sawaapplication.screens.notification.domain.useCases.ObserveUnreadNotificationsUseCase
import com.example.sawaapplication.screens.notification.domain.useCases.RemindUpcomingEventsUseCase
import com.example.sawaapplication.screens.notification.domain.useCases.RespondToReminderUseCase
import com.example.sawaapplication.screens.notification.domain.useCases.SendEventCreatedNotificationUseCase
import com.example.sawaapplication.screens.notification.domain.useCases.SendLikeNotificationUseCase
import com.example.sawaapplication.screens.notification.domain.useCases.SendProfileUpdateNotificationUseCase
import com.example.sawaapplication.screens.post.domain.model.Post
import com.example.sawaapplication.screens.profile.domain.useCases.GrantBadgeUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val fetchNotificationsUseCase: FetchNotificationsUseCase,
    private val markNotificationsAsReadUseCase: MarkNotificationsAsReadUseCase,
    private val sendProfileUpdateNotificationUseCase: SendProfileUpdateNotificationUseCase,
    private val sendEventCreatedNotificationUseCase: SendEventCreatedNotificationUseCase,
    private val notifyCommunityOfEventUseCase: NotifyCommunityOfEventUseCase,
    private val sendLikeNotificationUseCase: SendLikeNotificationUseCase,
    private val notificationPreferences: NotificationPreferences,
    private val observeUnreadNotificationsUseCase: ObserveUnreadNotificationsUseCase,
    private val remindUpcomingEventsUseCase: RemindUpcomingEventsUseCase,
    private val grantBadgeUseCase: GrantBadgeUseCase,
    private val getPendingRemindersUseCase: GetPendingRemindersUseCase,
    private val respondToReminderUseCase: RespondToReminderUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _hasUnreadNotifications = MutableStateFlow(true)
    val hasUnreadNotifications: StateFlow<Boolean> = _hasUnreadNotifications

    private val _reminders = MutableStateFlow<List<Notification>>(emptyList())
    val reminders: StateFlow<List<Notification>> = _reminders

    init {
        fetchNotifications()
        observeUnread()
        remindUpcomingEvents()

        val userId = auth.currentUser?.uid
        if (!userId.isNullOrBlank()) {
            loadReminders(userId)
        }
    }

    fun fetchNotifications() {
        fetchNotificationsUseCase { fetched ->
            //filter out any event_reminder types
            val filtered = fetched.filter { it.type != "event_reminder" }
            _notifications.value = filtered

            val hasUnread = fetched.any { !it.isRead }
            _hasUnreadNotifications.value = hasUnread
        }
    }

    fun markAsRead() {
        markNotificationsAsReadUseCase()
        _hasUnreadNotifications.value = false
        notificationPreferences.markAsSeen()
    }

    fun notifyProfileUpdate() {
        sendProfileUpdateNotificationUseCase()
    }

    fun notifyEventCreated(eventName: String) {
        sendEventCreatedNotificationUseCase(eventName)
    }

    fun notifyCommunityMembers(communityId: String, eventName: String) {
        notifyCommunityOfEventUseCase(communityId, eventName)
    }

    fun notifyLike(post: Post) {
        sendLikeNotificationUseCase(post)
    }

    fun remindUpcomingEvents() {
        remindUpcomingEventsUseCase()
    }

    private fun observeUnread() {
        observeUnreadNotificationsUseCase { hasUnread ->
            _hasUnreadNotifications.value = hasUnread
        }
    }

    fun loadReminders(userId: String) = viewModelScope.launch {
        _reminders.value = getPendingRemindersUseCase(userId)
    }

    fun answerReminder(userId: String, rem: Notification, attended: Boolean) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            respondToReminderUseCase(userId, rem.id, attended)
            if (attended && rem.eventId != null) {
                grantBadgeUseCase(userId)
            }
            loadReminders(userId)
        }
    }
}
package com.example.bantaybahay.Notifications

class NotificationPresenter(private val view: NotificationView) {

    private val repository = NotificationRepository()

    fun registerDeviceForNotifications() {
        repository.saveDeviceToken { success ->
            if (success) {
                view.onTokenRegistered()
            } else {
                view.onTokenRegistrationFailed()
            }
        }
    }
}

package io.inner.notification.models

import android.content.Intent

sealed class NotificationData {

    data class DeeplinkData(
            val deeplink: String
    ): NotificationData()

    class IntentData(
            val intents: Array<out Intent>
    ): NotificationData()

    class OpenMainData : NotificationData()
}

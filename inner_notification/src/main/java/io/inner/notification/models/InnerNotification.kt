package io.inner.notification.models

import io.inner.notification.config.NotificationConfig
import io.inner.notification.models.identity.NotificationIdentity

open class InnerNotification @JvmOverloads constructor(
        val data: NotificationData,
        val body: NotificationBody,
        val identity: NotificationIdentity = NotificationIdentity(),
        val config: NotificationConfig? = null
)
package io.inner.notification

import io.inner.notification.models.identity.NotificationIdentity

interface ExcludedRule {

    fun isExcluded(identity: NotificationIdentity): Boolean
}

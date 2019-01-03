package io.inner.notification

import io.inner.notification.models.identity.NotificationIdentity

class ExcludedRuleLambda(
        val lambda: (NotificationIdentity) -> Boolean
): ExcludedRule {

    override fun isExcluded(
            identity: NotificationIdentity
    ): Boolean  = lambda(identity)
}

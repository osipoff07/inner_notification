package io.inner.notification

import android.app.Activity
import android.app.Fragment
import android.view.View
import io.inner.notification.models.identity.NotificationIdentity

fun Activity.subscribeNotice() {
    NotificationManager.getInstance().register(this)
}

fun Activity.unsubscribeNotice() {
    NotificationManager.getInstance().unregister()
}

fun Activity.excludeWith(rule: ExcludedRule?) {
    NotificationManager.getInstance().excludeWith(rule)
}

fun Activity.excludeWith(lambda: (NotificationIdentity) -> Boolean) {
    NotificationManager.getInstance().excludeWith(ExcludedRuleLambda(lambda))
}

fun Fragment.excludeWith(rule: ExcludedRule?) {
    NotificationManager.getInstance().excludeWith(rule)
}

fun Fragment.excludeWith(lambda: (NotificationIdentity) -> Boolean) {
    NotificationManager.getInstance().excludeWith(ExcludedRuleLambda(lambda))
}

fun View.gone() {
    visibility = View.GONE
}
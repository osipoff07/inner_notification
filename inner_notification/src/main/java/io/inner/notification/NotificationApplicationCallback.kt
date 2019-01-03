package io.inner.notification

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import io.inner.notification.config.ManagerConfig

private var backStackActivitiesCount = 0
private var foregroundActivitiesCount = 0

fun isAppRunning(): Boolean = backStackActivitiesCount > 0

fun isAppInForeground(): Boolean = foregroundActivitiesCount > 0

class NotificationApplicationCallback : Application.ActivityLifecycleCallbacks {
    private val manager: NotificationManager

    constructor(manager: NotificationManager) {
        this.manager = manager
    }

    constructor(
            context: Context,
            config: ManagerConfig
    ) {
        manager = NotificationManager.register(context, config)
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        ++backStackActivitiesCount
    }

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityResumed(activity: Activity) {
        ++foregroundActivitiesCount
        manager.register(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        --foregroundActivitiesCount
        manager.unregister()
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle?) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        --backStackActivitiesCount
    }
}

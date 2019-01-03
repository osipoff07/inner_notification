package io.inner.notification.example

import android.app.Application
import io.inner.notification.NotificationApplicationCallback
import io.inner.notification.R
import io.inner.notification.config.ManagerConfig

class SampleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        val managerConfig = ManagerConfig(
                appIcon = R.drawable.ic_android,
                color = R.color.application_color,
                defaultSettingsName = R.string.app_name,
                sound = R.raw.aud
        )
        registerActivityLifecycleCallbacks(NotificationApplicationCallback(this, managerConfig))
    }
}

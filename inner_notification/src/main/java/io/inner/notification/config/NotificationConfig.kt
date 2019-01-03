package io.inner.notification.config

import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.RawRes

class NotificationConfig(
        val innerNotificationEnabled: Boolean? = null,
        val commonNotificationEnabled: Boolean? = null,
        @DrawableRes
        val appIcon: Int? = null,
        @ColorRes
        val color: Int? = null,
        @RawRes
        val sound: Int? = null
)

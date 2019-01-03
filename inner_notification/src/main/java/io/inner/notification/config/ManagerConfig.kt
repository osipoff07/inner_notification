package io.inner.notification.config

import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.RawRes
import android.support.annotation.StringRes

class ManagerConfig(
        val innerNotificationEnabled: Boolean = true,
        val commonNotificationEnabled: Boolean = true,
        val isNeedsToAddLaunchActivity: Boolean = true,
        @RawRes
        val sound: Int? = null,
        @StringRes
        val defaultSettingsName: Int,
        @DrawableRes
        val appIcon: Int,
        @ColorRes
        val color: Int
)

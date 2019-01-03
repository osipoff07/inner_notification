package io.inner.notification.models.identity

import android.support.annotation.StringRes

const val DEFAULT_IDENTIFIER = "none"

data class Type(
        val identifier: String = DEFAULT_IDENTIFIER,
        @StringRes
        val settingsName: Int? = null
)

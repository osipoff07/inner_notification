package io.inner.notification.models

import io.inner.notification.models.decoration.ImageDecoration
import io.inner.notification.models.decoration.TextDecoration

class NotificationBody(
        val titleDecoration: TextDecoration,
        val textDecoration: TextDecoration? = null,
        val imageDecoration: ImageDecoration? = null
)

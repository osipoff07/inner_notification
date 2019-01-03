package io.inner.notification.models.identity

import java.util.Date

const val DEFAULT_ID = -1L

data class NotificationIdentity @JvmOverloads constructor(
        val id: Long = DEFAULT_ID,
        val type: Type = Type(),
        val priority: Priority = Priority.DEFAULT
) {

    fun getId(): Int = if (id != DEFAULT_ID) {
        id
    } else {
        Date().time
    }.toInt()
}

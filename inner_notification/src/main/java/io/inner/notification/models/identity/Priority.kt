package io.inner.notification.models.identity

enum class Priority(val value: Int) {
    DEFAULT(3),
    HIGH(4),
    LOW(2),
    MAX(5),
    MIN(1),
    NONE(0),
    UNSPECIFIED(-1000)
}

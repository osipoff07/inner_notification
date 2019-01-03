package io.inner.notification.models.decoration

import android.content.Context
import android.support.annotation.StringRes

abstract class TextDecoration {
    abstract fun getText(context: Context): String
}

class ResTextDecoration(
        @StringRes
        val resId: Int
): TextDecoration() {

        override fun getText(
                context: Context
        ): String = context.resources.getString(resId)
}

class StringTextDecoration(
        val text: String
): TextDecoration() {

        override fun getText(
                context: Context
        ): String = text
}
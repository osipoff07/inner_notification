package io.inner.notification.models.decoration

import android.graphics.Bitmap
import android.support.annotation.DrawableRes

abstract class ImageDecoration

class ResImageDecoration(
        @DrawableRes
        val resId: Int
): ImageDecoration()

class BitmapImageDecoration(
        val bitmap: Bitmap
): ImageDecoration()

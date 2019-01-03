package io.inner.notification.events

import android.os.Handler
import android.view.MotionEvent
import android.view.View

private const val DEFAULT_DELAY = 5000L
private const val DEFAULT_TRANSITION_DURATION = 300L
private const val ZERO_ALPHA = 0f
private const val HALF_ALPHA = 0.5f
private const val FULL_ALPHA = 1f
private const val DEFAULT_SLIP = 10
private const val DEFAULT_VISIBLE_Y = 150f
private const val DEFAULT_TRANSITION = 100f
private const val DEFAULT_MOTION_SHIFT = 10f

class NotificationMotionEventListener(
        private val handler: Handler,
        private val runnable: Runnable
): View.OnTouchListener {

    private var isActive: Boolean = false
    private var dY: Float = ZERO_ALPHA

    override fun onTouch(
            view: View,
            motionEvent: MotionEvent
    ): Boolean = when (motionEvent.action) {
        MotionEvent.ACTION_DOWN -> onActionDown(view, motionEvent)
        MotionEvent.ACTION_MOVE -> onActionMove(view, motionEvent)
        MotionEvent.ACTION_UP -> onActionUp(view, motionEvent)
        else -> {
            view.performClick()
            false
        }
    }

    private fun onActionDown(
            view: View,
            motionEvent: MotionEvent
    ): Boolean {
        dY = motionEvent.rawY - view.y
        handler.removeCallbacks(runnable)

        return true
    }

    private fun onActionMove(
            view: View,
            motionEvent: MotionEvent
    ): Boolean {
        val tempPreTransition = motionEvent.rawY - dY
        if (tempPreTransition < ZERO_ALPHA) {
            view.translationY = tempPreTransition
            var tempAlpha = (DEFAULT_VISIBLE_Y + view.translationY)/ DEFAULT_VISIBLE_Y
            if (tempAlpha < ZERO_ALPHA) {
                tempAlpha = ZERO_ALPHA
            }
            view.alpha = tempAlpha
        } else {
            if (tempPreTransition < DEFAULT_TRANSITION) {
                view.translationY = tempPreTransition
            } else {
                view.translationY = DEFAULT_TRANSITION + (tempPreTransition/ DEFAULT_MOTION_SHIFT)
            }
        }

        return if (Math.abs(tempPreTransition) < DEFAULT_SLIP) {
            isActive = false
            false
        } else {
            isActive = true
            true
        }
    }

    private fun onActionUp(
            view: View,
            motionEvent: MotionEvent
    ): Boolean = if (isActive) {
        isActive = false
        if (view.alpha < HALF_ALPHA) {
            hideAndDestroy()
        } else {
            toDefaultPosition(view)
            handler.postDelayed(runnable, DEFAULT_DELAY)
        }
        true
    } else {
        view.performClick()
        false
    }

    private fun toDefaultPosition(view: View) {
        view.animate()
                .alpha(FULL_ALPHA)
                .translationY(ZERO_ALPHA)
                .setDuration(DEFAULT_TRANSITION_DURATION)
                .start()
    }

    private fun hideAndDestroy() {
        handler.post(runnable)
    }
}

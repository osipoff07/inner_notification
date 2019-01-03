package io.inner.notification.events

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.view.View
import io.inner.notification.models.InnerNotification
import io.inner.notification.models.NotificationData

class NotificationClickListener(
        private val applicationContext: Context,
        private val notification: InnerNotification,
        private val handler: Handler,
        private val runnable: Runnable
): View.OnClickListener {

    override fun onClick(view: View) {
        handler.post(runnable)
        val data = notification.data
        when (data) {
            is NotificationData.DeeplinkData -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.data = Uri.parse(data.deeplink)
                applicationContext.startActivity(intent)
            }
            is NotificationData.OpenMainData -> {
                val intent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                applicationContext.startActivity(intent)
            }
            is NotificationData.IntentData -> {
                data.intents.forEach {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                applicationContext.startActivities(data.intents)
            }
        }
    }
}

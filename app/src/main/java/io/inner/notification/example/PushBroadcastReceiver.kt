package io.inner.notification.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.inner.notification.NotificationSender
import io.inner.notif.taystynotification.R

class PushBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(
            context: Context,
            intent: Intent?
    ) {
        NotificationSender("Hello", Intent(context, ChatActivity::class.java))
                .setImage(R.drawable.image)
                .setText("sosos")
                .send()
    }
}

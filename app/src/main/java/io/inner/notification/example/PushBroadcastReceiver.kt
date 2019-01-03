package io.inner.notification.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.inner.notification.NotificationSender
import io.inner.notification.R
import io.inner.notification.models.identity.Priority

class PushBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(
            context: Context,
            intent: Intent?
    ) {
        NotificationSender("New message", Intent(context, ChatActivity::class.java))
                .setImage(R.drawable.image)
                .setText("How are you?")
                .setPriority(Priority.MAX)
                .send()
    }
}

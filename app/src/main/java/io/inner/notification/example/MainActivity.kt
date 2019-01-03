package io.inner.notification.example

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import io.inner.notification.NotificationSender
import io.inner.notification.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.main_activity_button).setOnClickListener {
            startActivity(Intent(baseContext, ChatActivity::class.java))
        }
        findViewById<Button>(R.id.main_activity_button_send_inner_notification).setOnClickListener {
            NotificationSender("Notification", Intent(baseContext, ChatActivity::class.java))
                    .setImage(R.drawable.image)
                    .setText("New message")
                    .send()
        }
        findViewById<Button>(R.id.main_activity_button_send_out_notification).setOnClickListener {
            NotificationSender("Notification", Intent(baseContext, ChatActivity::class.java))
                    .setImage(R.drawable.image)
                    .setText("New message")
                    .setInnerNotificationEnabled(false)
                    .send()
        }
    }
}

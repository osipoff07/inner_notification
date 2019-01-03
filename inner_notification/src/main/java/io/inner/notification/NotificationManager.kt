package io.inner.notification

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.support.annotation.LayoutRes
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.lang.ref.WeakReference
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.TextView
import io.inner.notif.taisty_notification.R
import io.inner.notification.models.decoration.BitmapImageDecoration
import io.inner.notification.models.InnerNotification
import io.inner.notification.config.ManagerConfig
import io.inner.notification.events.NotificationClickListener
import io.inner.notification.models.NotificationData
import io.inner.notification.models.decoration.ResImageDecoration
import io.inner.notification.events.NotificationMotionEventListener
import io.inner.notification.models.identity.NotificationIdentity
import io.inner.notification.models.identity.Priority

typealias AndroidNotificationManagerForChannel = android.app.NotificationManager

private val TAG = NotificationManager::class.java.simpleName
private const val DEFAULT_DELAY = 5000L
private const val DEFAULT_TRANSITION_DURATION = 300L
private const val ZERO_ALPHA = 0f
private const val FULL_ALPHA = 1f
private const val DEFAULT_VISIBLE_Y = 150f
private const val ZERO_TRANSITION = 0f
private const val DEFAULT_TRANSITION = 100f
private const val ANDROID_KEY = "android"
private const val DIMEN_KEY = "dimen"
private const val STATUS_BAR_HEIGHT_KEY = "status_bar_height"
private const val DEFAULT_REQUEST_CODE = -1

class NotificationManager private constructor(
        private val applicationContext: Context,
        private var config: ManagerConfig
) {
    private val sDefaultExcludedRule = object: ExcludedRule {
        override fun isExcluded(identity: NotificationIdentity) = false
    }
    private val isChannelSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    private var screen: WeakReference<Activity>? = null
    private var excludedRule: ExcludedRule = sDefaultExcludedRule
    private val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
    private val androidNotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? AndroidNotificationManagerForChannel

    companion object {
        private var sharedInstance: NotificationManager? = null

        fun register(
                context: Context,
                config: ManagerConfig
        ): NotificationManager = sharedInstance?.apply {
            Log.e(TAG, "NotificationManager is already exist")
            this.config = config
        } ?: NotificationManager(context.applicationContext, config).apply {
            sharedInstance = this
        }

        fun getInstance(): NotificationManager = sharedInstance
                ?: throw IllegalArgumentException("NotificationManager is not initialized")
    }

    fun register(activity: Activity) {
        screen = WeakReference(activity)
    }

    fun unregister() {
        screen = null
        excludedRule = sDefaultExcludedRule
    }

    fun excludeWith(rule: ExcludedRule?) {
        excludedRule = rule ?: sDefaultExcludedRule
    }

    fun sendNotification(notification: InnerNotification) {
        val currentActivity = screen?.get()
        if (currentActivity != null && isInnerNotificationEnabled(notification)) {
            sendInnerNotification(currentActivity, notification)
        } else if (isCommonNotificationEnabled(notification)) {
            sendCommonNotification(notification)
        } else {
            Log.w(TAG, "Notification is excluded $notification")
        }
    }

    fun cancelAll(id: Long) {
        notificationManagerCompat.cancelAll()
    }

    fun cancelBy(id: Long) {
        notificationManagerCompat.cancel(id.toInt())
    }

    @SuppressLint("NewApi")
    fun removeChannelBy(type: String) {
        if (!isChannelSupported) {

            return
        }

        androidNotificationManager?.deleteNotificationChannel(type)
    }

    @SuppressLint("NewApi")
    fun removeAllChannels() {
        if (!isChannelSupported) {

            return
        }
        val channels = androidNotificationManager?.notificationChannels ?: return

        channels.forEach {
            androidNotificationManager.deleteNotificationChannel(it.id)
        }
    }

    private fun isInnerNotificationEnabled(
            notification: InnerNotification
    ): Boolean = notification.config?.innerNotificationEnabled ?: config.innerNotificationEnabled

    private fun isCommonNotificationEnabled(
            notification: InnerNotification
    ): Boolean = notification.config?.commonNotificationEnabled ?: config.commonNotificationEnabled

    private fun sendCommonNotification(
            notification: InnerNotification
    ) {
        if (excludedRule.isExcluded(notification.identity)) {
            Log.w(TAG, "Notification is excluded $notification from common notification")

            return
        }
        val drawableIcon = notification.config?.appIcon ?: config.appIcon
        val colorId = notification.config?.color ?: config.color

        val color = ContextCompat.getColor(applicationContext, colorId)
        val channelId = notification.identity.type.identifier
        val builder = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(drawableIcon)
                .setColor(color)
                .setContentTitle(notification.body.titleDecoration.getText(applicationContext))
                .setContentText(notification.body.textDecoration?.getText(applicationContext))
                .setAutoCancel(true)
                .setPriority(convertPriorityToCompat(notification.identity.priority))

        createNotificationSoundUrl(notification)?.let {
            builder.setSound(it)
        }

        val data = notification.data
        val intents = when (data) {
            is NotificationData.DeeplinkData -> {
                createIntentsForCommonNotification(
                        Intent(Intent.ACTION_VIEW).apply {
                            this.data = Uri.parse(data.deeplink)
                        }
                )

            }
            is NotificationData.OpenMainData -> {
                createIntentsForCommonNotification(
                        applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
                )
            }
            is NotificationData.IntentData -> {
                createIntentsForCommonNotification(data.intents)
            }
        }
        intents.forEach {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivities(applicationContext, DEFAULT_REQUEST_CODE, intents, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)

        val imageDecoration = notification.body.imageDecoration
        when (imageDecoration) {
            is ResImageDecoration -> builder.setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, imageDecoration.resId))
            is BitmapImageDecoration -> builder.setLargeIcon(imageDecoration.bitmap)
        }
        openChannelIfNeeded(notification)
        notificationManagerCompat.notify(notification.identity.getId(), builder.build())
    }

    private fun createIntentsForCommonNotification(
            intent: Intent
    ): Array<out Intent> = if (isAppRunning() || !config.isNeedsToAddLaunchActivity) {
        arrayOf(intent)
    } else {
        val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)

        arrayOf(launchIntent, intent)
    }

    private fun createIntentsForCommonNotification(
            intents: Array<out Intent>
    ): Array<out Intent> = if (isAppRunning() || !config.isNeedsToAddLaunchActivity) {
        intents
    } else {
        val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
        arrayOf(launchIntent) + intents
    }

    private fun sendInnerNotification(
            currentActivity: Activity,
            notification: InnerNotification
    ) {
        if (excludedRule.isExcluded(notification.identity)) {
            Log.w(TAG, "Notification is excluded $notification from inner notification")

            return
        }
        val view = inflateNotificationView(R.layout.image_notification_layout)
        val rootView = currentActivity.window.decorView.rootView as ViewGroup

        val systemBarHeight = getSystemBarHeight(currentActivity)
        if (systemBarHeight > 0 && applicationContext.resources.configuration.orientation != Configuration.ORIENTATION_PORTRAIT) {
            view.layoutParams.width = rootView.width - systemBarHeight
        }
        rootView.addView(view)

        //populate notif
        view.findViewById<TextView>(R.id.sample_notification_title).text = notification.body.titleDecoration.getText(applicationContext)
        view.findViewById<TextView>(R.id.sample_notification_text).apply {
            notification.body.textDecoration?.getText(applicationContext)?.let {
                text = it
            } ?: gone()
        }
        view.findViewById<ImageView>(R.id.sample_notification_image_view).apply {
            val imageDecoration = notification.body.imageDecoration
            when (imageDecoration) {
                is ResImageDecoration -> setImageResource(imageDecoration.resId)
                is BitmapImageDecoration -> setImageBitmap(imageDecoration.bitmap)
                else -> gone()
            }
        }
        view.alpha = ZERO_ALPHA
        view.translationY = -DEFAULT_VISIBLE_Y
        view.animate()
                .alpha(FULL_ALPHA)
                .translationY(ZERO_TRANSITION)
                .setDuration(DEFAULT_TRANSITION_DURATION)
                .start()

        val handler = Handler()
        val runnable = createRemoveNotificationRunnable(view, handler)
        val clickListener = NotificationClickListener(applicationContext, notification, handler, runnable )
        view.setOnClickListener(clickListener)
        val eventListener = NotificationMotionEventListener(handler, runnable)
        view.setOnTouchListener(eventListener)
        handler.postDelayed(runnable, DEFAULT_DELAY)
    }

    private fun inflateNotificationView(@LayoutRes id: Int): View {
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(id, null)
        val layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        view.layoutParams = layoutParams
        view.setPadding(0, getStatusBarHeight(), 0, 0)

        return view
    }

    private fun createRemoveNotificationRunnable(
            view: View, handler: Handler
    ): Runnable = Runnable {
        view.animate()
                .alpha(ZERO_ALPHA)
                .translationY(-DEFAULT_TRANSITION)
                .setDuration(DEFAULT_TRANSITION_DURATION)
                .start()
        view.setOnClickListener(null)
        view.setOnTouchListener(null)
        handler.postDelayed(createRemoveViewRunnable(view), DEFAULT_TRANSITION_DURATION)
    }

    private fun createRemoveViewRunnable(view: View): Runnable = Runnable {
        val parent = view.parent as? ViewGroup
        parent?.removeView(view)
    }

    private fun getSystemBarHeight(activity: Activity): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val metrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(metrics)
            val usableHeight = metrics.widthPixels
            activity.windowManager.defaultDisplay.getRealMetrics(metrics)
            val realHeight = metrics.widthPixels

            return if (realHeight > usableHeight){
                realHeight - usableHeight
            } else {
                0
            }
        }

        return 0
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = applicationContext.resources.getIdentifier(STATUS_BAR_HEIGHT_KEY, DIMEN_KEY, ANDROID_KEY)
        if (resourceId > 0) {
            result = applicationContext.resources.getDimensionPixelSize(resourceId)
        }

        return result
    }

    @SuppressLint("NewApi")
    private fun openChannelIfNeeded(notification: InnerNotification) {
        if (!isChannelSupported) {

            return
        }
        androidNotificationManager?.getNotificationChannel(notification.identity.type.identifier)?.let {

            return
        }
        val nameRes = notification.identity.type.settingsName ?: config.defaultSettingsName
        val channel = NotificationChannel(
                notification.identity.type.identifier,
                applicationContext.getString(nameRes),
                notification.identity.priority.value
        )
        createNotificationSoundUrl(notification)?.let {
            val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            channel.setSound(it, audioAttributes)
        }
        androidNotificationManager?.notificationChannels
        androidNotificationManager?.createNotificationChannel(channel)
    }

    private fun convertPriorityToCompat(priority: Priority) = when (priority) {
        Priority.HIGH -> NotificationCompat.PRIORITY_HIGH
        Priority.LOW -> NotificationCompat.PRIORITY_LOW
        Priority.MAX -> NotificationCompat.PRIORITY_MAX
        Priority.MIN -> NotificationCompat.PRIORITY_MIN
        else -> NotificationCompat.PRIORITY_DEFAULT
    }

    private fun createNotificationSoundUrl(
            notification: InnerNotification
    ): Uri? {
        val soundRes = notification.config?.sound ?: config.sound ?: return null

        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + applicationContext.packageName + "/" + soundRes)
    }
}

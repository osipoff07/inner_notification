package io.inner.notification

import android.content.Intent
import android.graphics.Bitmap
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.RawRes
import android.support.annotation.StringRes
import io.inner.notification.config.NotificationConfig
import io.inner.notification.models.identity.DEFAULT_ID
import io.inner.notification.models.identity.Priority
import io.inner.notification.models.InnerNotification
import io.inner.notification.models.NotificationBody
import io.inner.notification.models.NotificationData
import io.inner.notification.models.identity.NotificationIdentity
import io.inner.notification.models.identity.Type
import io.inner.notification.models.decoration.BitmapImageDecoration
import io.inner.notification.models.decoration.ImageDecoration
import io.inner.notification.models.decoration.ResImageDecoration
import io.inner.notification.models.decoration.ResTextDecoration
import io.inner.notification.models.decoration.StringTextDecoration
import io.inner.notification.models.decoration.TextDecoration
import io.inner.notification.models.identity.DEFAULT_IDENTIFIER

class NotificationSender {
    //identity
    private var notificationIdentity: NotificationIdentity? = null
    private var id: Long? = null
    private var typeIdentifier: String = DEFAULT_IDENTIFIER
    @StringRes
    private var typeSettingsName: Int? = null
    private var priority: Priority = Priority.DEFAULT
    //data
    private val notificationData: NotificationData
    private var notificationBody: NotificationBody? = null
    private val titleDecoration: TextDecoration
    private var textDecoration: TextDecoration? = null
    private var imageDecoration: ImageDecoration? = null
    //config
    private var config: NotificationConfig? = null
    private var innerNotificationEnabled: Boolean? = null
    private var commonNotificationEnabled: Boolean? = null
    @DrawableRes
    private var appIcon: Int? = null
    @ColorRes
    private var color: Int? = null
    @RawRes
    private var sound: Int? = null

    constructor(title: String) {
        this.titleDecoration = StringTextDecoration(title)
        notificationData = NotificationData.OpenMainData()
    }

    constructor(@StringRes titleRes: Int) {
        this.titleDecoration = ResTextDecoration(titleRes)
        notificationData = NotificationData.OpenMainData()
    }

    constructor(title: String, url: String) {
        this.titleDecoration = StringTextDecoration(title)
        notificationData = NotificationData.DeeplinkData(url)
    }

    constructor(@StringRes titleRes: Int, url: String) {
        this.titleDecoration = ResTextDecoration(titleRes)
        notificationData = NotificationData.DeeplinkData(url)
    }

    constructor(title: String, vararg intents: Intent) {
        this.titleDecoration = StringTextDecoration(title)
        notificationData = NotificationData.IntentData(intents)
    }

    constructor(@StringRes titleRes: Int, vararg intents: Intent) {
        this.titleDecoration = ResTextDecoration(titleRes)
        notificationData = NotificationData.IntentData(intents)
    }

    constructor(title: String, intents: List<Intent>) {
        this.titleDecoration = StringTextDecoration(title)
        notificationData = NotificationData.IntentData(intents.toTypedArray())
    }

    constructor(@StringRes titleRes: Int, intents: List<Intent>) {
        this.titleDecoration = ResTextDecoration(titleRes)
        notificationData = NotificationData.IntentData(intents.toTypedArray())
    }

    fun setText(
            @StringRes textRes: Int
    ): NotificationSender = apply {
        textDecoration = ResTextDecoration(textRes)
    }

    fun setText(
            text: String
    ): NotificationSender = apply {
        this.textDecoration = StringTextDecoration(text)
    }

    fun setImage(
            @DrawableRes drawableId: Int
    ): NotificationSender = apply {
        imageDecoration = ResImageDecoration(drawableId)
    }

    fun setImage(
            bitmap: Bitmap
    ): NotificationSender = apply {
        imageDecoration = BitmapImageDecoration(bitmap)
    }

    fun setNotivicationBody(
            body: NotificationBody
    ) = apply {
        notificationBody = body
    }

    fun setItentity(
            identity: NotificationIdentity
    ): NotificationSender = apply {
        this.notificationIdentity = identity
    }

    fun setId(
            id: Long
    ): NotificationSender = apply {
        this.id = id
    }

    fun setTypeIdentifier(
            identifier: String
    ): NotificationSender = apply {
        typeIdentifier = identifier
    }

    fun setPriority(
            priority: Priority
    ): NotificationSender = apply {
        this.priority = priority
    }

    fun setTypeSettingsName(
            @StringRes settingsName: Int
    ): NotificationSender = apply {
        typeSettingsName = settingsName
    }

    fun setInnerNotificationEnabled(
            innerNotificationEnabled: Boolean
    ): NotificationSender = apply {
        this.innerNotificationEnabled = innerNotificationEnabled
    }

    fun setCommonNotificationEnabled(
            commonNotificationEnabled: Boolean
    ): NotificationSender = apply {
        this.commonNotificationEnabled = commonNotificationEnabled
    }

    fun setAppIcon(
            @DrawableRes appIcon: Int
    ): NotificationSender = apply {
        this.appIcon = appIcon
    }

    fun setColor(
            @ColorRes color: Int
    ): NotificationSender = apply {
        this.color = color
    }

    fun setSound(
            @RawRes sound: Int
    ): NotificationSender = apply {
        this.sound = sound
    }

    fun setConfig(
            config: NotificationConfig
    ): NotificationSender = apply {
        this.config = config
    }

    fun send() {
        val innerNotification = InnerNotification(
                data = notificationData,
                body = createBody(),
                identity = createIdentity(),
                config = createConfig()
        )
        NotificationManager.getInstance().sendNotification(innerNotification)
    }

    private fun createBody(): NotificationBody = notificationBody ?: NotificationBody(
            titleDecoration = titleDecoration,
            textDecoration = textDecoration,
            imageDecoration = imageDecoration
    )

    private fun createIdentity(): NotificationIdentity = notificationIdentity ?: NotificationIdentity(
            id ?: DEFAULT_ID,
            createType(),
            priority
    )

    private fun createType(): Type = Type(
            typeIdentifier,
            typeSettingsName
    )

    private fun createConfig(): NotificationConfig = config ?: NotificationConfig(
            innerNotificationEnabled,
            commonNotificationEnabled,
            appIcon,
            color,
            sound
    )
}

Info
------
#What is it?
This is simple library that worked with notifications! User can __disable notification from settings, but the application still gets them.__
Library can track your application activities and sending inner notifications, if the application in foreground.
Also library provide simple functionality to configure and sending common push notification with __channel support(API 26 and higher)__

![InnerNotification Sample](sample.gif)

Download:
--------
Gradle
```groovy
dependencies {
  implementation 'io.inner.notification:inner_notification:0.1.1'
}
```
Maven
```maven
<dependency>
  <groupId>io.inner.notification</groupId>
  <artifactId>inner_notification</artifactId>
  <version>0.1.1</version>
  <type>pom</type>
</dependency>
```

Using
-------
For using you need to create application class and register NotificationApplicationCallback to activity lifecycle:
```java
class SampleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        val managerConfig = ManagerConfig(
                appIcon = R.drawable.ic_android,
                color = R.color.application_color,
                defaultSettingsName = R.string.app_name,
                sound = R.raw.aud
        )
        registerActivityLifecycleCallbacks(NotificationApplicationCallback(this, managerConfig))
    }
}
```
For sending notification you need create NotificationSender, configure it and call method send():
```java
NotificationSender("You have new message", Intent(context, ChatActivity::class.java))
    .setImage(R.drawable.image)
    .setText("How are you?")
    .send()
```
You can configure push with actions:
1) Open Application
```java
NotificationSender(R.string.default_new_message)
```
2) Url(or deeplink)
```java
NotificationSender(R.string.default_new_message, "yourappscheme://open_post")
```
3) Array of Intents
```java
NotificationSender(
    R.string.default_new_message,
    Intent(context, ConversationsActivity::class.java),
    Intent(context, ChatActivity::class.java)
)
```

Configuration
--------------
#Library config
For configure Notification library you need to create ManagerConfig before initialization.
ManagerConfig fields:
- innerNotificationEnabled: Boolean - is need to send inner notifications, true by default
- commonNotificationEnabled: Boolean - is need to send common notification, true by default
- isNeedsToAddLaunchActivity: Boolean - is need to add launch activity(MainActivity), for opening common notification
- sound: Int? (@RawRes) - raw res of your notification sound, null by default
- defaultSettingsName: Int (@StringRes) - for Android O and higher, in settings user can configure notifications and it's channel name by default
- appIcon: Int (@DrawableRes) - res of your app icon
- color: Int (@ColorRes) - resource background color of application icon
#Notification config
All configuration that you need is available from NotificationSender
Otherwise you can create NotificationConfig and set it to NotificationSender
NotificationConfig fields:
- innerNotificationEnabled: Boolean - is need to send inner notifications, true by default
- commonNotificationEnabled: Boolean - is need to send common notification, true by default
- color: Int (@ColorRes) - resource background color of application icon
- appIcon: Int (@DrawableRes) - res of your app icon
- sound: Int? (@RawRes) - raw res of your notification sound, null by default
Priority of that class is higher than ManagerConfig.
#Identity
It's class to identify you push notification:
NotificationIdentity fields
- id: Long - push id, by default library creating id from system timestamp
- type: Type - contains information for channel supporting(type and user settings name)
- priority: Priority - priority of your notification, using by notification and by channel
#Excluding notifications
If you want to skip notification from some screen - you can exclude it by ExcludedRule
```java
//this code also available in Fragment
class SomeActivity: Activity() {

    override public fun onCreate(savedInstanceState: Bundle?) {
        //Kotlin example
        excludeWith {
             it.type.identifier == "chat"
        }
        //Java example
        NotificationExtensionKt.excludeWith(this, new ExcludedRule() {
              @Override
              public boolean isExcluded(@NotNull NotificationIdentity identity) {
                  return false;
              }
        });
    }
}
```
Add this code to enable notifications:
```java
excludeWith(null)
```

License
---------
MIT License

Copyright (c) 2019 Osipov Ilya (osipoff07)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

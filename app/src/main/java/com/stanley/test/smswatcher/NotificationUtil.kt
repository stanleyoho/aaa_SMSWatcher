package com.stanley.test.smswatcher

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat




object NotificationUtil {

    fun sendNotification(context : Context, message:String){
        val mNotificationManager =  (context.getSystemService(NOTIFICATION_SERVICE) as(NotificationManager))
        var channelCount = 1

        var clcChannel: NotificationChannel? = null
        var builder: NotificationCompat.Builder? = null
        val smsWatcherChannelId = "SMSWatcher"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            clcChannel = NotificationChannel(
                smsWatcherChannelId,
                smsWatcherChannelId,
                NotificationManager.IMPORTANCE_HIGH
            )
            clcChannel.description = "SMSWatcher"
            clcChannel.enableLights(true)
            clcChannel.enableVibration(true)
            mNotificationManager.createNotificationChannel(clcChannel)
            builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Test")
                .setContentText(message)
                .setAutoCancel(true)
                .setChannelId(smsWatcherChannelId)
        } else {
            builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Test")
                .setAutoCancel(true)
                .setContentText(message)
        }

        mNotificationManager.notify(1, builder.build())
    }
}
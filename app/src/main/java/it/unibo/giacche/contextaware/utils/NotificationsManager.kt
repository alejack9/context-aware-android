package it.unibo.giacche.contextaware.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import it.unibo.giacche.contextaware.utils.Constants.NOTIFICATION_CHANNEL_ID
import it.unibo.giacche.contextaware.utils.Constants.NOTIFICATION_CHANNEL_NAME
import it.unibo.giacche.contextaware.utils.Constants.NOTIFICATION_ID

class NotificationsManager(
    private val baseNotificationBuilder: NotificationCompat.Builder,
    private val notificationManager: NotificationManager
) {
    fun startForegroundService(service: Service) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel()

        service.startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    fun viewNotification() {
        notificationManager.notify(NOTIFICATION_ID, baseNotificationBuilder.build())
    }

    fun setText(text: String) {
        notificationManager.notify(NOTIFICATION_ID, baseNotificationBuilder.setContentText(text).build())
    }
}
package com.example.audioplayer.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.IBinder
import androidx.annotation.DrawableRes
import com.example.audioplayer.R

class AudioPlayerService : Service() {

    private val notificationManager by lazy { applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager }
    private var isStarted = false
    private var notification: Notification? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isStarted) {
            makeForeground()
            isStarted = true
        }

        return START_STICKY
    }

    private fun makeForeground() {

        notificationManager.createNotificationChannel(getNotificationChannel())

        notification = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.placeholder)
            .setStyle(Notification.DecoratedMediaCustomViewStyle())
            .addActionButton(PlayerConstants.ACTION_PREVIOUS, R.drawable.previous, "Previous")
            .addActionButton(PlayerConstants.ACTION_PLAY, R.drawable.play, "Play")
            .addActionButton(PlayerConstants.ACTION_PAUSE, R.drawable.pause, "Pause")
            .addActionButton(PlayerConstants.ACTION_NEXT, R.drawable.next, "Next")
            .addActionButton(PlayerConstants.ACTION_STOP_SERVICE, R.drawable.close, "Close")
            .setOngoing(true)
            .build()

        startForeground(ONGOING_ID, notification)
    }

    private fun Notification.Builder.addActionButton(
        actionConst: String,
        @DrawableRes icon: Int,
        title: String
    ): Notification.Builder {
        val intent = Intent(actionConst)
        val bIntent = PendingIntent.getBroadcast(
            this@AudioPlayerService,
            PlayerConstants.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return addAction(Notification.Action.Builder(
            Icon.createWithResource(this@AudioPlayerService, icon),
            title,
            bIntent
        ).build())
    }

    private fun getNotificationChannel(): NotificationChannel =
        NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            .apply { lockscreenVisibility = Notification.VISIBILITY_PUBLIC }

    companion object {
        private const val CHANNEL_NAME = "NotificationChannelName"
        private const val CHANNEL_ID = "1001"
        private const val ONGOING_ID = 101
    }
}
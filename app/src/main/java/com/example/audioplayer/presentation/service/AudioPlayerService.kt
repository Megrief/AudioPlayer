package com.example.audioplayer.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.audioplayer.R

class AudioPlayerService : Service() {

    private val notifManager by lazy { applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager }

    private var isStarted = false
    private val binder = AudioBinder()
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (!isStarted) {
            makeForeground()
            isStarted = true
        }


        return START_STICKY
    }

    private fun makeForeground() {

        val remoteViews = RemoteViews(packageName, R.layout.player_card)

        notifManager.createNotificationChannel(getNotificationChannel())

        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setCustomContentView(remoteViews)
        }.build()
        startForeground(ONGOING_ID, notification)
    }

    private fun getNotificationChannel(): NotificationChannel =
        NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            .apply { lockscreenVisibility = Notification.VISIBILITY_PUBLIC }

    inner class AudioBinder : Binder() {
        fun getService(): AudioPlayerService {
            return this@AudioPlayerService
        }
    }

    companion object {
        private const val CHANNEL_NAME = "NotificationChannelName"
        private const val CHANNEL_ID = "1001"
        private const val ONGOING_ID = 101
    }
}
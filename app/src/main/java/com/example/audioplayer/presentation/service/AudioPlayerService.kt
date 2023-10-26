package com.example.audioplayer.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Binder
import android.os.IBinder
import androidx.annotation.DrawableRes
import com.example.audioplayer.R
import com.example.audioplayer.data.PlayerManager
import com.example.audioplayer.presentation.activity.MainActivity
import org.koin.android.ext.android.getKoin

class AudioPlayerService : Service() {

    private val playerManager: PlayerManager = getKoin().get()

    private val notifManager by lazy { applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager }

    private var isStarted = false
    private val binder = AudioBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isStarted) {
            when (intent?.action) {
                MainActivity.START_INTENT -> makeForeground()
                PLAY -> { }
                PAUSE -> { }
                PREV -> { }
                NEXT -> { }
            }
            isStarted = true
        }


        return START_STICKY
    }

    private fun makeForeground() {

        notifManager.createNotificationChannel(getNotificationChannel())

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.placeholder)
            .setStyle(Notification.DecoratedMediaCustomViewStyle())
            .setContentTitle("Track's name")
            .setContentText("Artist's name")
            .setLargeIcon(Icon.createWithResource(this, R.drawable.placeholder))
            .addActionButton(PREV, R.drawable.previous, "Previous")
            .addActionButton(PLAY, R.drawable.play, "Play")
            .addActionButton(PAUSE, R.drawable.pause, "Pause")
            .addActionButton(NEXT, R.drawable.next, "Next")
            .setOngoing(true)
            .build()

        startForeground(ONGOING_ID, notification)
    }

    private fun Notification.Builder.addActionButton(
        actionConst: String,
        @DrawableRes icon: Int,
        title: String
    ): Notification.Builder {
        val intent = Intent(this@AudioPlayerService, AudioPlayerService::class.java)
            .apply { action = actionConst }
        val pauseIntent = PendingIntent.getService(
            this@AudioPlayerService,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        return addAction(Notification.Action.Builder(
            Icon.createWithResource(this@AudioPlayerService, icon),
            title,
            pauseIntent
        ).build())
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
        private const val REQUEST_CODE = 0

        private const val PLAY = "PLAY"
        private const val PAUSE = "PAUSE"
        private const val NEXT = "NEXT"
        private const val PREV = "PREV"
    }
}
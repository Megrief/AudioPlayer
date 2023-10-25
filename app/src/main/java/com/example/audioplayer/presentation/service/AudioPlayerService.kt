package com.example.audioplayer.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.audioplayer.R
import com.example.audioplayer.presentation.activity.MainActivity

class AudioPlayerService : Service() {
    private val notifChannelName = "NotificationChannelName"
    private val notifChannelId = "1001"
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
        val track = intent?.getStringExtra("TRACK_NAME") ?: "nope"
        val artist = intent?.getStringExtra("ARTIST_NAME") ?: "nope"
        if (!isStarted) {
            makeForeground(track, artist)
            isStarted = true
        }


        return START_STICKY
    }

    private fun makeForeground(trackName: String, artistName: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pending = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notifChannel = NotificationChannel(
            notifChannelId,
            notifChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        val remoteViews = RemoteViews(packageName, R.layout.player_card)
        notifChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notifManager.createNotificationChannel(notifChannel)

        val notification = NotificationCompat.Builder(this, notifChannelId).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setCustomContentView(remoteViews)
        }.build()
        startForeground(101, notification)
    }

    inner class AudioBinder : Binder() {
        fun getService(): AudioPlayerService {
            return this@AudioPlayerService
        }
    }


}
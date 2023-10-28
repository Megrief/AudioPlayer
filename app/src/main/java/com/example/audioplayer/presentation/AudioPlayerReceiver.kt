package com.example.audioplayer.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.audioplayer.data.PlayerManager
import com.example.audioplayer.presentation.service.AudioPlayerService
import com.example.audioplayer.presentation.service.PlayerConstants

class AudioPlayerReceiver(
    private val playerManager: PlayerManager
) : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            PlayerConstants.ACTION_START_SERVICE -> { onStartService(context) }
            PlayerConstants.ACTION_STOP_SERVICE -> { onStopService(context) }
            PlayerConstants.ACTION_PLAY -> { playerManager.playOnService() }
            PlayerConstants.ACTION_PAUSE -> { playerManager.pause() }
            PlayerConstants.ACTION_PREVIOUS -> { playerManager.previous() }
            PlayerConstants.ACTION_NEXT -> { playerManager.next() }

        }
    }

    private fun onStartService(context: Context) {
        val serviceIntent = Intent(context, AudioPlayerService::class.java)
        context.startService(serviceIntent)
    }

    private fun onStopService(context: Context) {
        val serviceIntent = Intent(context, AudioPlayerService::class.java)
        context.stopService(serviceIntent)
        playerManager.stop()

    }
}
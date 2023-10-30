package com.example.audioplayer.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.audioplayer.presentation.service.PlayerConstants

class AudioPlayerReceiver : BroadcastReceiver() {

    var onStart: (() -> Unit)? = null
    var onStop: (() -> Unit)? = null
    var onPlay: (() -> Unit)? = null
    var onPause: (() -> Unit)? = null
    var onNext: (() -> Unit)? = null
    var onPrevious: (() -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            PlayerConstants.ACTION_START_SERVICE -> { onStart?.invoke() }
            PlayerConstants.ACTION_PLAY -> { onPlay?.invoke() }
            PlayerConstants.ACTION_PAUSE -> { onPause?.invoke() }
            PlayerConstants.ACTION_PREVIOUS -> { onPrevious?.invoke() }
            PlayerConstants.ACTION_NEXT -> { onNext?.invoke() }
            PlayerConstants.ACTION_STOP_SERVICE -> { onStop?.invoke() }
        }
    }
}
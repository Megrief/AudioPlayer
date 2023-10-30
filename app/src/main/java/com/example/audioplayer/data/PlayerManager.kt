package com.example.audioplayer.data

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

class PlayerManager(
    private val context: Context
) {
    private var lastUri: Uri? = null
    private var currentState = PlayerState.DEFAULT
    private var mediaPlayer: MediaPlayer? = null
    var onCompletion: (() -> Unit)? = null

    fun play(uri: Uri) {
        if (uri != lastUri) {
            releaseResources()
            lastUri = uri
            recreate()
            mediaPlayer?.start()
        } else {
            if (currentState == PlayerState.PAUSED) {
                mediaPlayer?.start()
            }
        }
        currentState = PlayerState.PLAYING
    }

    fun pause() {
        if (currentState == PlayerState.PLAYING) {
            mediaPlayer?.pause()
            currentState = PlayerState.PAUSED
        }
    }

    private fun recreate() {
        mediaPlayer = MediaPlayer.create(context, lastUri)
        mediaPlayer?.setOnCompletionListener {
            onCompletion?.invoke()
        }
    }

    fun releaseResources() {
        if (currentState == PlayerState.PLAYING || currentState == PlayerState.PAUSED) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            currentState = PlayerState.DEFAULT
        }
    }

    private enum class PlayerState {
        PLAYING,
        PAUSED,
        DEFAULT
    }
}
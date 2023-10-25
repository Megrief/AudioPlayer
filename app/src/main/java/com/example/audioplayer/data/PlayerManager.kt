package com.example.audioplayer.data

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class PlayerManager(
    private val mediaPlayer: MediaPlayer,
    private val storageManager: StorageManager,
    private val context: Context
) {

    private var currentState = DEFAULT

    fun configurePlayer(
        onCompletionListener: () -> Unit
    ) {
        mediaPlayer.setOnPreparedListener {
            currentState = PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            it.reset()
            currentState = DEFAULT
            onCompletionListener()
        }
    }

    fun play(id: Long) {

        CoroutineScope(Dispatchers.IO + Job()).launch {
            if (currentState == PLAYING || currentState == PAUSED) {
                mediaPlayer.stop()
                mediaPlayer.reset()
            }
            val uri = getUriById(id)
            if (uri != null) mediaPlayer.setDataSource(context, uri)
            mediaPlayer.prepare()
            mediaPlayer.start()
            currentState = PLAYING
        }
    }

    fun pause() {
        if (currentState == PLAYING) {
            mediaPlayer.pause()
            currentState = PAUSED
        }
    }

    private suspend fun getUriById(id: Long): Uri? {
        return storageManager.getUriById(id).single()
    }

    companion object {
        private const val DEFAULT = 0
        private const val PREPARED = 1
        private const val PLAYING = 2
        private const val PAUSED = 3
    }
}
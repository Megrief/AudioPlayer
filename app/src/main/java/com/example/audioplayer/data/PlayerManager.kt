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
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var currentState = DEFAULT

    private var _onNextListener: (() -> Long)? = null
    private val onNextListener: () -> Long
        get() = _onNextListener!!

    private var _onPreviousListener: (() -> Long)? = null
    private val onPreviousListener: () -> Long
        get() = _onPreviousListener!!

    init {
        mediaPlayer.setOnPreparedListener {
            currentState = PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            it.reset()
            currentState = DEFAULT
            scope.launch {
                val uri = storageManager.getUriById(onNextListener()).single()
                if (uri != null) play(uri)
            }
        }
    }

    fun configurePlayer(
        onNextListener: () -> Long,
        onPreviousListener: () -> Long
    ) {
        _onNextListener = onNextListener
        _onPreviousListener = onPreviousListener
    }

    fun next() {
        scope.launch {
            val uri = storageManager.getUriById(onNextListener()).single()
            if (uri != null) play(uri)
        }
    }

    fun previous() {
        scope.launch {
            val uri = storageManager.getUriById(onPreviousListener()).single()
            if (uri != null) play(uri)
        }
    }

    fun play(uri: Uri) {
        if (currentState == PLAYING || currentState == PAUSED) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            currentState = DEFAULT
        }
        mediaPlayer.setDataSource(context, uri)
        mediaPlayer.prepare()
        mediaPlayer.start()
        currentState = PLAYING
    }

    fun playOnService() {
        if (currentState == PAUSED) {
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

    fun stop() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        currentState = DEFAULT
    }

    companion object {
        private const val DEFAULT = 0
        private const val PREPARED = 1
        private const val PLAYING = 2
        private const val PAUSED = 3
    }
}
package com.example.audioplayer.presentation.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.audioplayer.R
import com.example.audioplayer.databinding.ActivityMainBinding
import com.example.audioplayer.presentation.AudioPlayerReceiver
import com.example.audioplayer.presentation.activity.adapter.TrackAdapter
import com.example.audioplayer.presentation.service.AudioPlayerService
import com.example.audioplayer.presentation.service.PlayerConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }
    private val bReceiver: AudioPlayerReceiver by inject()
    private val viewModel: MainVM by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
        setContentView(binding.root)
        setButtonsEnabled(false)
        viewModel.registerReceiver(applicationContext)
        setCallbacksBR()
        viewModel.configurePlayer()
        configurePlayerView()
        configureContentList()
    }


    private fun configureContentList() {
        binding.contentList.adapter = TrackAdapter {
            lifecycleScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    setButtonsEnabled(true)
                }
                viewModel.play(it.id)
                viewModel.startService(applicationContext)
            }
        }
        viewModel.listContent.observe(this@MainActivity) { trackList ->
            (binding.contentList.adapter as TrackAdapter).setContent(trackList)
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
                results[android.Manifest.permission.READ_MEDIA_AUDIO]?.also { isGranted ->
                    if (isGranted) viewModel.getContent()
                    else Toast.makeText(this, getString(R.string.need_permission_media), Toast.LENGTH_SHORT).show()
                }

                results[android.Manifest.permission.POST_NOTIFICATIONS]?.also { isGranted ->
                    if (!isGranted) Toast.makeText(this, getString(R.string.need_permission_notifications), Toast.LENGTH_SHORT).show()
                }
            }
            requestPermission.launch(arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO, android.Manifest.permission.POST_NOTIFICATIONS))
        }
    }

    private fun configurePlayerView() {
        with(binding) {
            playButton.setOnClickListener {
                val intent = Intent(PlayerConstants.ACTION_PLAY)
                sendBroadcast(intent)
            }
            pauseButton.setOnClickListener {
                val intent = Intent(PlayerConstants.ACTION_PAUSE)
                sendBroadcast(intent)
            }
            previousButton.setOnClickListener {
                val intent = Intent(PlayerConstants.ACTION_PREVIOUS)
                 sendBroadcast(intent)
            }
            nextButton.setOnClickListener {
                val intent = Intent(PlayerConstants.ACTION_NEXT)
                sendBroadcast(intent)
            }
        }
    }

    private fun setButtonsEnabled(isEnabled: Boolean) {
        with(binding) {
            playButton.isEnabled = isEnabled
            pauseButton.isEnabled = isEnabled
            nextButton.isEnabled = isEnabled
            previousButton.isEnabled = isEnabled
        }
    }

    private fun setCallbacksBR() {
        with(bReceiver) {
            val serviceIntent = Intent(this@MainActivity, AudioPlayerService::class.java)
            onStart = { startService(serviceIntent) }
            onPlay = { viewModel.play() }
            onPause = { viewModel.pause() }
            onNext = { viewModel.next() }
            onPrevious = { viewModel.previous() }
            onStop = {
                stopService(serviceIntent)
                if (this@MainActivity.isDestroyed) viewModel.stop()
            }
        }
    }
}
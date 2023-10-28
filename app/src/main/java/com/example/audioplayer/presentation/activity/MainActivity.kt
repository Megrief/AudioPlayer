package com.example.audioplayer.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.audioplayer.databinding.ActivityMainBinding
import com.example.audioplayer.presentation.activity.adapter.TrackAdapter
import com.example.audioplayer.presentation.service.PlayerConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }

    private val viewModel: MainVM by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setButtonsEnabled(false)
        viewModel.registerReceiver(applicationContext)
        viewModel.configurePlayer()
        configurePlayerView()
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

}
package com.example.audioplayer.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.audioplayer.databinding.ActivityMainBinding
import com.example.audioplayer.presentation.activity.adapter.TrackAdapter
import com.example.audioplayer.presentation.service.AudioPlayerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }

    private val serviceIntent: Intent by lazy { Intent(this, AudioPlayerService::class.java) }

    private val viewModel: MainVM by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding) {
            contentList.adapter = TrackAdapter {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.getById(it.id).let { track ->
                        withContext(Dispatchers.Main) {
                            serviceIntent.putExtra("TRACK_NAME", track?.trackName)
                                .putExtra("ARTIST_NAME", track?.artistName)
                            startService(serviceIntent)
                            Toast.makeText(this@MainActivity, "Track name is ${ track?.trackName }", Toast.LENGTH_SHORT).show()
                        }
                    }
                    viewModel.play(it.id)
                }
            }
            viewModel.listContent.observe(this@MainActivity) { trackList ->
                (contentList.adapter as TrackAdapter).setContent(trackList)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.bind(this)
    }

    override fun onStop() {
        super.onStop()
        viewModel.unbind(this)
    }

}
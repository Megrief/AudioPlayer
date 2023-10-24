package com.example.audioplayer.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.audioplayer.presentation.activity.adapter.TrackAdapter
import com.example.audioplayer.databinding.ActivityMainBinding
import com.example.audioplayer.presentation.service.AudioPlayerService
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
//                startService(serviceIntent)
                Toast.makeText(this@MainActivity, "Track name is ${ it.trackName}", Toast.LENGTH_SHORT).show()
            }
            viewModel.listContent.observe(this@MainActivity) { trackList ->
                (contentList.adapter as TrackAdapter).setContent(trackList)
            }
        }


    }

}